package io.github.blamebutton.breadbox.command;

import io.github.blamebutton.breadbox.util.Environment;
import io.github.blamebutton.breadbox.util.I18n;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static io.github.blamebutton.breadbox.BreadboxApplication.instance;

@BreadboxCommand("help")
public class HelpCommand implements ICommand {

    private static Logger logger = LoggerFactory.getLogger(HelpCommand.class);

    @Override
    public void handle(IMessage message, CommandLine commandLine) {
        IChannel channel = message.getChannel();
        if (commandLine.hasOption('h')) {
            channel.sendMessage("help dialog: " + getOptions().toString());
            return;
        }
        List<String> args = commandLine.getArgList();
        String command = args.size() > 0 ? args.get(0) : null;
        EmbedBuilder builder = buildEmbedObject(command);
        if (builder == null) {
            sendUnknownCommandMessage(message, command);
            return;
        }
        sendRespondingMessage(message, builder);
        logger.debug(Arrays.toString(args.toArray()));
    }

    /**
     * Get the help channel to which the message should be sent.
     *
     * @param message the message object from which the command came
     * @return the channel to send it to
     */
    private IChannel getHelpChannel(IMessage message) {
        if (message == null) {
            return null;
        }
        if (Environment.PRODUCTION.equals(instance.getEnvironment())) {
            return RequestBuffer.request(() -> {
                IUser author = message.getAuthor();
                return message.getClient().getOrCreatePMChannel(author);
            }).get();
        }
        return message.getChannel();
    }

    /**
     * Send the message for an unknown command.
     *
     * @param message the message from the help message
     */
    private void sendUnknownCommandMessage(IMessage message, String command) {
        RequestBuffer.request(() -> {
            IChannel channel = getHelpChannel(message);
            channel.sendMessage(I18n.get("command.help.single.not_found", command));
        });
    }

    /**
     * Send the responding message.
     *
     * @param message the message
     * @param builder the builder
     */
    private void sendRespondingMessage(IMessage message, EmbedBuilder builder) {
        RequestBuffer.request(() -> {
            IChannel channel = getHelpChannel(message);
            if (!channel.isPrivate()) {
                message.delete();
            }
            channel.sendMessage(builder.build());
        });
    }

    /**
     * Build embed object from arguments.
     *
     * @param command the command
     * @return the embed builder
     */
    private EmbedBuilder buildEmbedObject(String command) {
        EmbedBuilder builder = new EmbedBuilder();
        if (command != null && instance.commandExists(command)) {
            ICommand cmd = instance.getCommand(command);
            HelpFormatter formatter = new HelpFormatter();
            StringWriter usageWriter = new StringWriter();
            StringWriter optionsWriter = new StringWriter();
            Options options = cmd.getOptions() != null ? cmd.getOptions() : new Options();
            formatter.printUsage(new PrintWriter(usageWriter), 200, command, options);
            String usage = cmd.getUsage() != null ? command + " " + cmd.getUsage() : usageWriter.toString();
            formatter.printOptions(new PrintWriter(optionsWriter), 80, options, 2, 4);
            String[] split = optionsWriter.toString().split("\n");
            builder.withTitle(I18n.get("command.help.single.embed.title", command))
                    .withDesc(MessageFormat.format("{0}\n\n", cmd.getDescription()))
                    .appendDesc(MessageFormat.format("`{0}`\n\n", usage));
            Arrays.stream(split).forEach(value -> builder.appendDesc("`" + value + "`\n"));
        } else {
            builder.withTitle(I18n.get("command.help.embed.title"))
                    .withDescription(I18n.get("command.help.embed.description"));
            instance.getCommands().forEach((commandName, value) -> {
                String usage = String.format("%s %s", commandName, value.getUsage());
                builder.appendField(String.format("%s: %s", commandName, usage), value.getDescription(), false);
            });
        }
        return builder;
    }

    @Override
    public String getUsage() {
        return I18n.get("command.help.usage");
    }

    @Override
    public String getDescription() {
        return I18n.get("command.help.description");
    }
}
