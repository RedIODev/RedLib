package dev.redio.service;

public interface ServiceScope extends AutoCloseable {

    ServiceProvider serviceProvider();

    @Override
    default void close() {
        if (serviceProvider() instanceof ClosableServiceProvider csp)
            csp.close();
    }
}
