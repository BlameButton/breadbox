package io.github.blamebutton.breadbox.exception;

public class InvalidValidatorException extends RuntimeException {
    public InvalidValidatorException(Class<?> validatorClass) {
        super(String.format("Invalid validator \"%s\" does not implement IValidator", validatorClass.getSimpleName()));
    }
}
