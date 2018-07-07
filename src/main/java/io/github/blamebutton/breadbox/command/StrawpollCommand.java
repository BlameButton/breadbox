package io.github.blamebutton.breadbox.command;

import io.github.blamebutton.breadbox.util.I18n;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.text.MessageFormat;

/**
 * Command for creating strawpolls.
 *
 * <pre>
 * Usage: ?strawpoll &lt;options>
 * </pre>
 */
@BreadboxCommand("poll")
public class StrawpollCommand implements ICommand {

    private static final Logger logger = LoggerFactory.getLogger(StrawpollCommand.class);

    @Override
    public void handle(IMessage message, CommandLine cli) {
        IGuild guild = message.getGuild();
        IUser author = message.getAuthor();
        String displayName = author.getDisplayName(guild);
        String defaultTitle = MessageFormat.format("Strawpoll by {0}", displayName);
        EmbedBuilder builder = new EmbedBuilder()
                .withTitle(cli.getOptionValue('t', defaultTitle));
        String[] optionValues = cli.getOptionValues('o');
        if (cli.hasOption('o') && optionValues.length >= 2) {
            for (String value: optionValues) {
                builder.appendField("Option", value, true);
            }
            message.getChannel().sendMessage(builder.build());
        } else {
            message.getChannel().sendMessage(I18n.get("command.strawpoll.two_options_required"));
        }
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getDescription() {
        return I18n.get("command.strawpoll.description");
    }

    @Override
    public Options getOptions() {
        Options options = new Options();
        options.addOption(Option.builder("t").argName("title").hasArg(true).desc("Title for this strawpoll").build());
        options.addOption(Option.builder("o").argName("option").hasArg(true).desc("Add an option to the strawpoll").build());
        return options;
    }
}
