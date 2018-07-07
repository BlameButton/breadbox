package io.github.blamebutton.breadbox;

import io.github.blamebutton.breadbox.command.ICommand;
import io.github.blamebutton.breadbox.handler.CommandHandler;
import io.github.blamebutton.breadbox.handler.ReadyEventHandler;
import io.github.blamebutton.breadbox.util.Environment;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

import java.util.HashMap;
import java.util.Map;

public class BreadboxApplication {

    public static BreadboxApplication instance;

    private final String token;
    private final Map<String, ICommand> commands;
    private Environment environment;
    private IDiscordClient client;
    private CommandHandler commandHandler;

    {
        commands = new HashMap<>();
        commandHandler = new CommandHandler();
    }

    private BreadboxApplication() {
        this(System.getenv("BREADBOX_TOKEN"));
    }

    /**
     * Mainly used for testing.
     *
     * @param token the token to use
     */
    BreadboxApplication(String token) {
        this.token = token;
        setup(token);
    }

    /**
     * Run the Discord Bot.
     *
     * @param args the command line parameters
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            instance = new BreadboxApplication(args[0]);
        } else {
            instance = new BreadboxApplication();
        }
    }

    /**
     * Get a command from the {@link BreadboxApplication#commands} hashmap.
     *
     * @param command the command name
     * @return the command instance
     */
    public ICommand getCommand(String command) {
        return commands.get(command);
    }

    /**
     * Get all the commands.
     *
     * @return all the commands
     */
    public Map<String, ICommand> getCommands() {
        return commands;
    }

    String getToken() {
        return token;
    }

    /**
     * Set up the bot, connect to Discord API.
     *
     * @param token the Discord bot token
     */
    private void setup(String token) {
        environment = Environment.find(System.getenv("BREADBOX_ENV"));
        client = BreadboxAuthentication.createClient(token);
        if (client != null) {
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(new ReadyEventHandler());
            dispatcher.registerListener(commandHandler);
        }
    }

    /**
     * Register a command using an instance.
     *
     * @param command the command name
     * @param klass   the command instance
     */
    public void registerCommand(String command, ICommand klass) {
        if (klass == null || klass.getDescription() == null) {
            throw new IllegalArgumentException("Command usage and description cannot be null.");
        }
        commands.put(command, klass);
    }

    /**
     * Register a command using it's class, constructor must not be private and not have any paramaters.
     *
     * @param command the command name
     * @param klass   the command class
     * @param <T>     the type of the command
     */
    public <T extends ICommand> void registerCommand(String command, Class<T> klass) {
        try {
            T cmdInstance = klass.getConstructor().newInstance();
            registerCommand(command, cmdInstance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public IDiscordClient getClient() {
        return client;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public boolean commandExists(String command) {
        return getCommand(command) != null;
    }
}
