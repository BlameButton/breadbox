package io.github.blamebutton.breadbox;

import io.github.blamebutton.breadbox.command.BreadboxCommand;
import io.github.blamebutton.breadbox.util.Environment;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BreadboxApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(BreadboxApplicationTest.class);

    private String token;
    private BreadboxApplication app;

    @BeforeEach
    void setUp() {
        token = System.getenv("BREADBOX_TOKEN");
        if (app == null) {
            app = new BreadboxApplication(token);
        }
    }

    @Test
    void main() {
        assertEquals(app.getToken(), token, "Token did not equal.");
    }

    @Test
    void getCommand() {
        String commandName = "get-command-test";

        app.registerCommand(commandName, new BreadboxCommand() {
            @Override
            public void handle(IMessage message, List<String> args) {
            }

            @Override
            public String getUsage() {
                return "usage";
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        BreadboxCommand command = app.getCommand(commandName);
        assertNotNull(command);
        assertEquals(command.getUsage(), "usage");
    }

    @Test
    void getCommands() {
        String commandName = "get-commands-test";

        app.registerCommand(commandName, null);
        Map<String, BreadboxCommand> commands = app.getCommands();
        boolean contains = commands.containsKey(commandName);
        assertTrue(contains);
    }

    @Test
    void getToken() {
    }

    @Test
    void registerCommand() {
        String commandName = "register-command-test";
        app.registerCommand(commandName, new BreadboxCommand() {
            @Override
            public void handle(IMessage message, List<String> args) {
                throw new NotImplementedException("Not implemented.");
            }

            @Override
            public String getUsage() {
                return "test-command <arg> [arg]";
            }

            @Override
            public String getDescription() {
                return "Command for testing purposes.";
            }
        });

        Map<String, BreadboxCommand> commands = app.getCommands();

        boolean contains = commands.containsKey(commandName);
        BreadboxCommand command = commands.get(commandName);
        assertTrue(contains);
        assertEquals(command.getUsage(), "test-command <arg> [arg]");
        assertEquals(command.getDescription(), "Command for testing purposes.");
        assertThrows(NotImplementedException.class, () -> command.handle(null, null));
    }

    @Test
    void getEnvironment() {
        assertEquals(Environment.LOCAL, app.getEnvironment());
    }
}