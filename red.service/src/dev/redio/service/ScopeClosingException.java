package dev.redio.service;

public class ScopeClosingException extends RuntimeException {

    public ScopeClosingException(Exception[] suppressedExceptions) {
        this(null, suppressedExceptions);
    }

    public ScopeClosingException(String message, Exception[] suppressedExceptions) {
        super(message);
        for (Exception exception : suppressedExceptions)
            addSuppressed(exception);
    }
    
}
