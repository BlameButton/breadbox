package io.github.blamebutton.breadbox.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Interface for any command.
 */
public interface ICommand {

    /**
     * Handle the execution of a command.
     *
     * @param message     message object that called the command
     * @param commandLine command line instance
     */
    void handle(IMessage message, CommandLine commandLine);

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

    /**
     * Get the options of this command.
     *
     * @return the options
     */
    default Options getOptions() {
        return null;
    }
}
