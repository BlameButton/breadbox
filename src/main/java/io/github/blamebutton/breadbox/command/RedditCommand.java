package io.github.blamebutton.breadbox.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import sx.blah.discord.handle.obj.IMessage;

public class RedditCommand implements ICommand {

    @Override
    public void handle(IMessage message, CommandLine commandLine) {

    }

    @Override
    public String getUsage() {
        return "sr <subreddit>, user <user>, post <post> + <hour;day;week;month;year;all>";
    }

    @Override
    public String getDescription() {
        return "A command for retreiving info from reddit (subreddit, post, user)";
    }

    @Override
    public Options getOptions() {
        return null;
    }
}
