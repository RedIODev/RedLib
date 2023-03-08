package dev.redio.concurrent.task;

public final class Context {
    
    private final Waker waker;
    public Context(Waker waker) {
       this.waker = waker;
    }

    public Waker waker() {
        return waker;
    }
}
