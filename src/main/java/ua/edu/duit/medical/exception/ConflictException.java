package ua.edu.duit.medical.exception;

public final class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(409, message);
    }
}

