package io.github.blamebutton.breadbox.validator;

import io.github.blamebutton.breadbox.command.ICommand;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandValidator {

    Class<? extends ICommand> value();
}

