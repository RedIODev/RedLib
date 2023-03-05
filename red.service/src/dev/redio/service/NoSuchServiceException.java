package dev.redio.service;

public class NoSuchServiceException extends RuntimeException {

    public NoSuchServiceException() {
    }

    public NoSuchServiceException(String message) {
        super(message);
    }

    public NoSuchServiceException(Throwable cause) {
        super(cause);
    }

    public NoSuchServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
