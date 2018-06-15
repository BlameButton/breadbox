package io.github.blamebutton.breadbox.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.List;

public class RedditCommand implements BreadboxCommand{
    @Override
    public void handle(IMessage message, List<String> args) {

    }

    @Override
    public String getUsage() {
        return "sr <subreddit>, user <user>, post <post> + <hour;day;week;month;year;all>";
    }

    @Override
    public String getDescription() {
        return "A command for retreiving info from reddit (subreddit, post, user)";
    }
}
