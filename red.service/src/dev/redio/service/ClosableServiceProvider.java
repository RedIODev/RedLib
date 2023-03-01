package dev.redio.service;

public interface ClosableServiceProvider extends ServiceProvider, AutoCloseable {

    @Override
    void close();

    boolean isClosed();
}
