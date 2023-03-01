package dev.redio.service;

import java.util.Objects;
import java.util.SortedSet;

import dev.redio.util.SideChannel;

public interface ServiceProvider { 

    public static <T> T getDependency(Class<T> service) {
        Objects.requireNonNull(service);
        var loader = SideChannel.acquire(ServiceProvider.class);
        return loader.getService(service);
    }

    public static <T> SortedSet<ServiceDescriptor<T>> getDependencies(Class<T> service) {
        Objects.requireNonNull(service);
        var loader = SideChannel.acquire(ServiceProvider.class);
        return loader.getServices(service);
    }

    public <T> T getService(Class<T> service);

    public <T> SortedSet<ServiceDescriptor<T>> getServices(Class<T> service);

    public ServiceScope createScope();
}
