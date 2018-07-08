package io.github.blamebutton.breadbox.validator;

import io.github.blamebutton.breadbox.command.HelpCommand;
import org.apache.commons.cli.CommandLine;

@CommandValidator(HelpCommand.class)
public class HelpCommandValidator implements IValidator {

    @Override
    public ValidationResult validate(CommandLine cli) {
        ValidationResult result = new ValidationResult();
        result.addError("Test error #1");
        result.addError("Test error #2");
        return result;
    }
}
