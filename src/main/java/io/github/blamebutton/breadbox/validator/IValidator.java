package io.github.blamebutton.breadbox.validator;

import org.apache.commons.cli.CommandLine;

public interface IValidator {

    /**
     * The validate function that has been called.
     *
     * @param cli the command line instance
     * @return the validation result
     */
    ValidationResult validate(CommandLine cli);
}