package ua.edu.duit.medical.exception;

public final class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(400, message);
    }
}

