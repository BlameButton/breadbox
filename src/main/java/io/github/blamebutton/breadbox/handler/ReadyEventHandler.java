package io.github.blamebutton.breadbox.handler;

import io.github.blamebutton.breadbox.BreadboxApplication;
import io.github.blamebutton.breadbox.command.BreadboxCommand;
import io.github.blamebutton.breadbox.command.ICommand;
import io.github.blamebutton.breadbox.exception.InvalidValidatorException;
import io.github.blamebutton.breadbox.validator.CommandValidator;
import io.github.blamebutton.breadbox.validator.IValidator;
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

import static io.github.blamebutton.breadbox.BreadboxApplication.instance;

public class ReadyEventHandler implements IListener<ReadyEvent> {

    private static final String name = "BreadBox";
    private static final String packageName = BreadboxApplication.class.getPackage().getName();
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
        try {
            registerCommands();
            registerValidators();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register all the application commands.
     */
    private void registerCommands() throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(BreadboxCommand.class);
        for (Class<?> annotatedClass : annotatedClasses) {
            Object reflectionInstance = annotatedClass.newInstance();
            if (reflectionInstance instanceof ICommand) {
                BreadboxCommand annotation = annotatedClass.getAnnotation(BreadboxCommand.class);
                String commandName = annotation.value();
                instance.registerCommand(commandName, (ICommand) reflectionInstance);
                logger.info("Registered command: {}", commandName);
            }
        }
    }

    /**
     * Register all validators for the commands.
     */
    private void registerValidators() throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CommandValidator.class);
        for (Class<?> validatorClass : annotatedClasses) {
            CommandValidator annotation = validatorClass.getAnnotation(CommandValidator.class);
            Class<? extends ICommand> command = annotation.value();
            if (command.isAnnotationPresent(BreadboxCommand.class)) {
                BreadboxCommand commandAnnotation = command.getAnnotation(BreadboxCommand.class);
                Object validatorInstance = validatorClass.newInstance();
                if (validatorInstance instanceof IValidator) {
                    String commandName = commandAnnotation.value();
                    String simpleName = validatorClass.getSimpleName();
                    instance.addValidatorForCommand(((IValidator) validatorInstance), commandName);
                    logger.info("Registered validator {} for command {}", simpleName, commandName);
                } else {
                    throw new InvalidValidatorException(validatorClass);
                }
            }
        }
    }
}
