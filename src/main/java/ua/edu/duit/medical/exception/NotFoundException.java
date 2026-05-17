package ua.edu.duit.medical.exception;

public final class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(404, message);
    }
}

