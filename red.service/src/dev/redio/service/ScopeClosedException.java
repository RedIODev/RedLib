package dev.redio.service;

public class ScopeClosedException extends RuntimeException {

    public ScopeClosedException() {
    }

    public ScopeClosedException(String message) {
        super(message);
    }

    public ScopeClosedException(Throwable cause) {
        super(cause);
    }

    public ScopeClosedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
