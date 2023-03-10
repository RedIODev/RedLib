package dev.redio.internal.concurrent;

public class AsyncRuntimeError extends Error {

    public AsyncRuntimeError() {
    }

    public AsyncRuntimeError(String message) {
        super(message);
    }

    public AsyncRuntimeError(Throwable cause) {
        super(cause);
    }

    public AsyncRuntimeError(String message, Throwable cause) {
        super(message, cause);
    }
    
}
