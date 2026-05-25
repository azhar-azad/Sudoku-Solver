package com.azhar.sudoku.solver;

public class ValidationResult {

    private final boolean valid;
    private final String error;

    public ValidationResult(boolean valid, String error) {
        this.valid = valid;
        this.error = error;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult fail(String error) {
        return new ValidationResult(false, error);
    }

    public boolean isValid() {
        return valid;
    }

    public String getError() {
        return error;
    }
}
