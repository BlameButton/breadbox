package io.github.blamebutton.breadbox.command;

import io.github.blamebutton.breadbox.BreadboxApplication;
import io.github.blamebutton.breadbox.util.Environment;
import io.github.blamebutton.breadbox.util.I18n;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static io.github.blamebutton.breadbox.BreadboxApplication.instance;

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
        RequestBuffer.request(() -> {
            EmbedObject embed = buildEmbedObject(args);
            if (Environment.PRODUCTION.equals(instance.getEnvironment())) {
                IUser author = message.getAuthor();
                IPrivateChannel pm = message.getClient().getOrCreatePMChannel(author);
                pm.sendMessage(embed);
            } else {
                channel.sendMessage(embed);
                if (!channel.isPrivate()) {
                    message.delete();
                }
            }
        });
        logger.debug(Arrays.toString(args.toArray()));
    }

    /**
     * Build embed object from arguments.
     *
     * @param args the arguments
     * @return the embed builder
     */
    private EmbedObject buildEmbedObject(List<String> args) {
        EmbedBuilder builder = new EmbedBuilder();
        if (args.size() > 0) {
            String command = args.get(0);
            ICommand cmd = BreadboxApplication.instance.getCommand(command);
            HelpFormatter formatter = new HelpFormatter();
            StringWriter usageWriter = new StringWriter();
            StringWriter optionsWriter = new StringWriter();
            formatter.printUsage(new PrintWriter(usageWriter), 200, command, cmd.getOptions());
            String usage = cmd.getUsage() != null ? command + " " + cmd.getUsage() : usageWriter.toString();
            formatter.printOptions(new PrintWriter(optionsWriter), 80, cmd.getOptions(), 1, 2);
            builder.withTitle(I18n.get("command.help.single.embed.title", command))
                    .withDescription(MessageFormat.format("{0}\n\n", cmd.getDescription()))
                    .appendDescription(MessageFormat.format("```{0}```\n", usage))
                    .appendDescription("```" + optionsWriter.toString() + "```");
        } else {
            builder.withTitle(I18n.get("command.help.embed.title"))
                    .withDescription(I18n.get("command.help.embed.description"));
            BreadboxApplication.instance.getCommands().forEach((command, instance) -> {
                String usage = String.format("%s %s", command, instance.getUsage());
                builder.appendField(String.format("%s: %s", command, usage), instance.getDescription(), false);
            });
        }
        return builder.build();
    }

    @Override
    public String getUsage() {
        return I18n.get("command.help.usage");
    }

    @Override
    public String getDescription() {
        return I18n.get("command.help.description");
    }

    @Override
    public Options getOptions() {
        return new Options();
    }
}
