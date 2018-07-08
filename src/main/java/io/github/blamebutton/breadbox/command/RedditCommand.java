package io.github.blamebutton.breadbox.command;

import io.github.blamebutton.breadbox.util.I18n;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import sx.blah.discord.handle.obj.IMessage;

@BreadboxCommand("reddit")
public class RedditCommand implements ICommand {

    @Override
    public void handle(IMessage message, CommandLine cli) {
        if (!cli.hasOption('q')) {
            message.getChannel().sendMessage(I18n.get("command.reddit.optionqrequired"));
        }
        if (cli.getOptionValue('q').length() > 512) {
            message.getChannel().sendMessage(I18n.get("command.reddit.option_q_too_long"));
        }
        if (cli.hasOption("tp")) {
            String tpInput = cli.getOptionValue("tp").toLowerCase();
            if (!tpInput.equals("sr") || !tpInput.equals("link") || !tpInput.equals("user")) {
                message.getChannel().sendMessage(I18n.get("command.reddit.option_tp_should_be"));
            }
        } else {

        }
    }

    @Override
    public String getUsage() {
        return I18n.get("command.reddit.usage");
    }

    @Override
    public String getDescription() {
        return I18n.get("command.reddit.description");
    }

    @Override
    public Options getOptions() {
        Options options = new Options();
        options.addOption(Option.builder("q").argName("query").hasArg(true).desc("Search query string of max 512 characters").build());
        options.addOption(Option.builder("tp").argName("type").hasArg(true).desc("Search type (sr, link, user)").build());
        options.addOption(Option.builder("t").argName("time").hasArg(true).desc("Search time (hour, day, week, month, all)").build());
        options.addOption(Option.builder("s").argName("sort").hasArg(true).desc("Search sort (relevance, hot, top, new, comments)").build());
        return options;
    }
}
