package dev.redio.internal.service;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dev.redio.service.ServiceContainer;
import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;
import dev.redio.service.ServiceScope;

public class ServiceProviderImpl implements ServiceProvider {

    private final Map<Class<?>, SortedSet<ServiceDescriptor<?>>> descriptors;
    private final ServiceContainer container;

    public ServiceProviderImpl(ServiceContainer c) {
        this.descriptors = c.getDescriptors();
        this.container = c;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> service) {
        var set = descriptors.get(service);
        if (set.isEmpty())
            return null;
        return (T) set.first().getService();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> SortedSet<ServiceDescriptor<T>> getServices(Class<T> service) {
        SortedSet<ServiceDescriptor<T>> result = new TreeSet<>(ServiceDescriptor.DESCRIPTOR_COMPARATOR);
        result.addAll((SortedSet<? extends ServiceDescriptor<T>>) descriptors.get(service));
        return Collections.unmodifiableSortedSet(result);
    }

    @Override
    public ServiceScope createScope() {
        return new ServiceScopeImpl(new ScopedServiceProviderImpl(container, this));
    }

}
