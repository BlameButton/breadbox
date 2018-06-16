package io.github.blamebutton.breadbox.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.List;

/**
 * Interface for any command.
 */
public interface BreadboxCommand {

    /**
     * Handle the execution of a command.
     *
     * @param message the message object
     * @param args    all arguments for the command
     */
    void handle(IMessage message, List<String> args);

    /**
     * Get the usage of this command
     *
     * @return the usage
     */
    String getUsage();

    /**
     * Get the description for a command.
     *
     * @return the description
     */
    String getDescription();
}
