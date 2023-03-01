package dev.redio.internal.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import dev.redio.service.ClosableServiceProvider;
import dev.redio.service.ScopeClosedException;
import dev.redio.service.ServiceContainer;
import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;
import dev.redio.service.ServiceScope;

public class ScopedServiceProviderImpl implements ClosableServiceProvider {

    private final Map<Class<?>, Set<ServiceDescriptor<?>>> descriptors = new HashMap<>();
    private final ServiceContainer container;
    private final ServiceProvider parent;
    private boolean isClosed;

    public ScopedServiceProviderImpl(ServiceContainer c, ServiceProvider parent) {
        this.container = c;
        this.parent = parent;
        for (var entry : c.getScopedDescriptors().entrySet()) {
            Set<ServiceDescriptor<?>> providerSet = entry.getValue()
                    .stream()
                    .map(ssd -> ssd.getDescriptor(this))
                    .collect(Collectors.toSet());
            descriptors.put(entry.getKey(), providerSet);
        }
    }

    @Override
    public ServiceScope createScope() {
        if (isClosed())
            throw new ScopeClosedException("Cannot create child scope on a closed scope");
        return new ServiceScopeImpl(new ScopedServiceProviderImpl(container, this));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> service) {
        if (isClosed())
            throw new ScopeClosedException("Cannot get service from a closed scope");
        var set = descriptors.get(service);
        if (set == null || set.isEmpty())
            return parent.getService(service);
        return (T)set.iterator().next().getService();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> SortedSet<ServiceDescriptor<T>> getServices(Class<T> service) {
        if (isClosed())
            throw new ScopeClosedException("Cannot get services from a closed scope");
        SortedSet<ServiceDescriptor<T>> result = new TreeSet<>(ServiceDescriptor.DESCRIPTOR_COMPARATOR);
        result.addAll((Set<? extends ServiceDescriptor<T>>) descriptors.get(service));
        result.addAll(parent.getServices(service));
        return Collections.unmodifiableSortedSet(result);
    }

    @Override
    public void close() {
        if (!isClosed)
            isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed || (parent instanceof ClosableServiceProvider c && c.isClosed());
    }

}
