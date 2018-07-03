package io.github.blamebutton.breadbox.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utils class for retrieving messages.
 */
public final class I18n {

    private static ResourceBundle bundle;

    static {
        bundle = ResourceBundle.getBundle("messages");
    }

    private I18n() {
        // Utils class should not be constructed
    }

    /**
     * Get a message.
     *
     * @param key the key to search for
     * @return the message or if the key was not found, return <code>: key :</code>
     */
    public static String get(String key) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return ": " + key + " :";
    }

    /**
     * Get a message.
     *
     * @param key  the key to search for
     * @param vars the variables in the string
     * @return the message or if the key was not found, return <code>: key :</code>
     */
    public static String get(String key, Object... vars) {
        String message = get(key);
        return MessageFormat.format(message, vars);
    }
}
