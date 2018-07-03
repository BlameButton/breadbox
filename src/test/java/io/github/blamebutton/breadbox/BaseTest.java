package io.github.blamebutton.breadbox;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    static BreadboxApplication app;
    static String token;

    @BeforeAll
    static void setUp() {
        token = System.getenv("BREADBOX_TOKEN");
        if (app == null) {
            app = new BreadboxApplication(token);
        }
    }

    @AfterAll
    static void tearDown() {
        token = null;
        app = null;
    }
}
