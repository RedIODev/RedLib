package dev.redio.internal.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;

import dev.redio.service.ScopedServiceDescriptor;
import dev.redio.service.ServiceContainer;
import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;

public class ServiceContainerImpl implements ServiceContainer {

    private final Map<Class<?>, Set<ScopedServiceDescriptor<?>>> scopedDescriptors = new HashMap<>();
    private final Map<Class<?>, SortedSet<ServiceDescriptor<?>>> descriptors = new HashMap<>();

    private final ServiceProvider provider;

    public ServiceContainerImpl() {
        this.provider = new ServiceProviderImpl(this);
        addSingleton(ServiceContainer.class, this);
        addSingleton(ServiceProvider.class, provider);
    }

    @Override
    public Map<Class<?>, Set<ScopedServiceDescriptor<?>>> getScopedDescriptors() {
        return Collections.unmodifiableMap(scopedDescriptors);
    }

    @Override
    public Map<Class<?>, SortedSet<ServiceDescriptor<?>>> getDescriptors() {
        return Collections.unmodifiableMap(descriptors);
    }

    @Override
    public ServiceProvider getServiceProvider() {
        return provider;
    }

    @Override
    public <T> boolean addTransient(Class<T> service) {
        var desc = ServiceDescriptorImpl.Transient.createTransient(provider, service);
        return addDescriptor(service, desc.orElse(null));
    }

    @Override
    public <T> boolean addTransient(Class<T> service, Class<? extends T> implementation) {
        var desc = ServiceDescriptorImpl.Transient.createTransient(provider, service, implementation);
        return addDescriptor(service, desc.orElse(null));
    }

    @Override
    public <T> boolean addTransient(Class<T> service, Predicate<Class<? extends T>> filter) {
        var desc = ServiceDescriptorImpl.Transient.createTransient(provider, service, filter);
        return addDescriptor(service, desc.orElse(null));
    }

    @Override
    public <T> boolean addSingleton(Class<T> service) {
        var desc = ServiceDescriptorImpl.Singleton.createSingleton(provider, service);
        return addDescriptor(service, desc.orElse(null));
    }

    @Override
    public <T> boolean addSingleton(Class<T> service, Class<? extends T> implementation) {
        var desc = ServiceDescriptorImpl.Singleton.createSingleton(provider, service, implementation);
        return addDescriptor(service, desc.orElse(null));
    }

    @Override
    public <T> boolean addSingleton(Class<T> service, T instance) {
        var descriptor = ServiceDescriptorImpl.Singleton.createSingleton(service, instance);
        return addDescriptor(service, descriptor);
    }

    @Override
    public <T> boolean addSingleton(Class<T> service, Predicate<Class<? extends T>> filter) {
        var desc = ServiceDescriptorImpl.Singleton.createSingleton(provider, service, filter);
        return addDescriptor(service, desc.orElse(null));
    }

    @Override
    public <T> boolean addScoped(Class<T> service) {
        var descriptor = ScopedServiceDescriptorImpl.createScoped(service);
        return addScopedDescriptor(service, descriptor);
    }

    @Override
    public <T> boolean addScoped(Class<T> service, Class<? extends T> implementation) {
        var descriptor = ScopedServiceDescriptorImpl.createScoped(service, implementation);
        return addScopedDescriptor(service, descriptor);
    }

    @Override
    public <T> boolean addScoped(Class<T> service, Predicate<Class<? extends T>> filter) {
        var descriptor = ScopedServiceDescriptorImpl.createScoped(service, filter);
        return addScopedDescriptor(service, descriptor);
    }

    private boolean addDescriptor(Class<?> service, ServiceDescriptor<?> descriptor) {
        if (descriptor == null)
            return false;
        var set = descriptors.computeIfAbsent(service, s -> new TreeSet<>(ServiceDescriptor.DESCRIPTOR_COMPARATOR));
        return set.add(descriptor);
    }

    private boolean addScopedDescriptor(Class<?> service, ScopedServiceDescriptor<?> descriptor) {
        if (descriptor == null)
            return false;
        var set = scopedDescriptors.computeIfAbsent(service, s -> new HashSet<>());
        return set.add(descriptor);
    }
}
