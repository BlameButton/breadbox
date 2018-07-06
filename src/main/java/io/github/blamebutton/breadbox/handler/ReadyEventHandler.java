package io.github.blamebutton.breadbox.handler;

import io.github.blamebutton.breadbox.BreadboxApplication;
import io.github.blamebutton.breadbox.command.BreadboxCommand;
import io.github.blamebutton.breadbox.command.ICommand;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.RequestBuffer;

import java.util.Set;

public class ReadyEventHandler implements IListener<ReadyEvent> {

    private static final String name = "BreadBox";
    private static Logger logger = LoggerFactory.getLogger(ReadyEventHandler.class);

    @Override
    public void handle(ReadyEvent event) {
        IDiscordClient client = event.getClient();
        String applicationName = client.getApplicationName();
        logger.info("Current application name '{}'", applicationName);
        if (!client.getOurUser().getName().equals(applicationName)) {
            RequestBuffer.request(() -> client.changeUsername(name));
            logger.debug("Changed name from {} to {}", event.getClient().getOurUser().getName(), name);
        } else {
            logger.debug("Name already matches.");
        }
        RequestBuffer.request(() ->
                client.changePresence(StatusType.ONLINE, ActivityType.WATCHING, "your nudes (?help)"));
        registerCommands();
    }

    /**
     * Register all the application commands.
     */
    private void registerCommands() {
        BreadboxApplication instance = BreadboxApplication.instance;
        String packageName = BreadboxApplication.class.getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(BreadboxCommand.class);
        for (Class<?> annotatedClass : annotatedClasses) {
            try {
                Object reflectionInstance = annotatedClass.newInstance();
                if (reflectionInstance instanceof ICommand) {
                    BreadboxCommand annotation = annotatedClass.getAnnotation(BreadboxCommand.class);
                    String commandName = annotation.value();
                    instance.registerCommand(commandName, (ICommand) reflectionInstance);
                    logger.info("Registered command: {}", commandName);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
