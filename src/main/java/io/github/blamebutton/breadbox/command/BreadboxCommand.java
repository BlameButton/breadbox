package io.github.blamebutton.breadbox.command;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BreadboxCommand {

    /**
     * The command name.
     *
     * @return the command name
     */
    String value();
}
