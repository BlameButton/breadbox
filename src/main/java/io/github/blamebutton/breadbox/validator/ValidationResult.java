package io.github.blamebutton.breadbox.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private List<String> errors = new ArrayList<>();

    /**
     * Get a list of all errors.
     *
     * @return the list of errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Set a list of all errors
     *
     * @param errors the list of errors
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Check if the validation result contains any errors.
     *
     * @return if the result was valid
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Add an error to the error messages array.
     *
     * @param message the error message
     */
    void addError(String message) {
        errors.add(message);
    }
}
