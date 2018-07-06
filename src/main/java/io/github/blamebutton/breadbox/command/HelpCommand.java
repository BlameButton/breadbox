package io.github.blamebutton.breadbox.command;

import io.github.blamebutton.breadbox.BreadboxApplication;
import io.github.blamebutton.breadbox.util.Environment;
import io.github.blamebutton.breadbox.util.I18n;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

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
            EmbedBuilder builder = buildEmbedObject(args);
            if (Environment.PRODUCTION.equals(instance.getEnvironment())) {
                IUser author = message.getAuthor();
                IPrivateChannel pm = message.getClient().getOrCreatePMChannel(author);
                pm.sendMessage(builder.build());
            } else {
                channel.sendMessage(builder.build());
                if (!channel.isPrivate()) {
                    message.delete();
                }
            }
        });
        logger.debug(Arrays.toString(args.toArray()));
    }

    private EmbedBuilder buildEmbedObject(List<String> args) {
        EmbedBuilder builder;
        if (args.size() > 0) {
            String command = args.get(0);
            ICommand cmd = BreadboxApplication.instance.getCommand(command);
            builder = new EmbedBuilder()
                    .withTitle(I18n.get("command.help.single.embed.title", command));
            builder.appendField(I18n.get("command.help.single.usage_field", command, cmd.getUsage()), cmd.getDescription(), true);
        } else {
            builder = new EmbedBuilder()
                    .withTitle(I18n.get("command.help.embed.title"))
                    .withDescription(I18n.get("command.help.embed.description"));
            BreadboxApplication.instance.getCommands().forEach((command, instance) -> {
                String usage = String.format("%s %s", command, instance.getUsage());
                builder.appendField(String.format("%s: %s", command, usage), instance.getDescription(), false);
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
