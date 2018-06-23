package io.github.blamebutton.breadbox.util;

import java.util.Arrays;

/**
 * Define the bot environment.
 */
public enum Environment {
    LOCAL("local"), PRODUCTION("prod");

    private final String text;

    Environment(String text) {
        this.text = text;
    }

    public static Environment find(String text) {
        if (text == null) return LOCAL;
        return Arrays.stream(values())
                .filter(environment -> environment.text.equals(text))
                .findFirst().orElse(LOCAL);
    }
}
