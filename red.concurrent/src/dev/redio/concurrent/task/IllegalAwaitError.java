package dev.redio.concurrent.task;

public class IllegalAwaitError extends Error {

    public IllegalAwaitError() {
    }

    public IllegalAwaitError(String message) {
        super(message);
    }

    public IllegalAwaitError(Throwable cause) {
        super(cause);
    }

    public IllegalAwaitError(String message, Throwable cause) {
        super(message, cause);
    }
    
}
