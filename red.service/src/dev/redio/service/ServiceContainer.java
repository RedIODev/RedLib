package dev.redio.service;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Predicate;

import dev.redio.internal.service.ServiceContainerImpl;

public interface ServiceContainer {

    static ServiceContainer defaultContainer() {
        return new ServiceContainerImpl();
    }

    Map<Class<?>, Set<ScopedServiceDescriptor<?>>> getScopedDescriptors();

    Map<Class<?>, SortedSet<ServiceDescriptor<?>>> getDescriptors();

    ServiceProvider getServiceProvider();

    <T> boolean addTransient(Class<T> service);

    <T> boolean addTransient(Class<T> service, Class<? extends T> implementation);

    <T> boolean addTransient(Class<T> service, Predicate<Class<? extends T>> filter);

    <T> boolean addSingleton(Class<T> service);

    <T> boolean addSingleton(Class<T> service, Class<? extends T> implementation);

    <T> boolean addSingleton(Class<T> service, Predicate<Class<? extends T>> filter);

    <T> boolean addSingleton(Class<T> service, T instance);

    <T> boolean addScoped(Class<T> service);

    <T> boolean addScoped(Class<T> service, Class<? extends T> implementation);

    <T> boolean addScoped(Class<T> service, Predicate<Class<? extends T>> filter);
}
