package io.github.blamebutton.breadbox.command;

import io.github.blamebutton.breadbox.util.I18n;
import org.apache.commons.cli.CommandLine;
import sx.blah.discord.handle.obj.IMessage;

@BreadboxCommand("reddit")
public class RedditCommand implements ICommand {

    @Override
    public void handle(IMessage message, CommandLine commandLine) {

    }

    @Override
    public String getUsage() {
        return I18n.get("command.reddit.usage");
    }

    @Override
    public String getDescription() {
        return I18n.get("command.reddit.description");
    }
}
