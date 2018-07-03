package io.github.blamebutton.breadbox.util;

import org.slf4j.Logger;

import java.util.UUID;

/**
 * Utils class for reporting incidents internally.
 */
public final class IncidentUtils {


    private IncidentUtils() {
        // Util classes should not be constructed.
    }

    public static String report(String message, Logger logger, Throwable exception) {
        String incidentId = UUID.randomUUID().toString().toUpperCase();
        logger.error(String.format("Incident: '%s' - %s, '%s'", incidentId, message, exception.getMessage()), exception);
        return incidentId;
    }
}
