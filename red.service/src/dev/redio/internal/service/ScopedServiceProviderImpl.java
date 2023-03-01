package dev.redio.internal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import dev.redio.service.ClosableServiceProvider;
import dev.redio.service.ScopeClosedException;
import dev.redio.service.ScopeClosingException;
import dev.redio.service.ScopedServiceDescriptor;
import dev.redio.service.ServiceContainer;
import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;
import dev.redio.service.ServiceScope;

public class ScopedServiceProviderImpl implements ClosableServiceProvider {

    private final Map<Class<?>, Set<ServiceDescriptor<?>>> descriptors = new HashMap<>();
    private Map<Class<?>, Set<ScopedServiceDescriptor<?>>> loadedScopedDescriptors;
    private final ServiceContainer container;
    private final ServiceProvider parent;
    private boolean isClosed;

    public ScopedServiceProviderImpl(ServiceContainer c, ServiceProvider parent) {
        this.container = c;
        this.parent = parent;
        refreshServices();
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
        refreshServices();
        var set = descriptors.get(service);
        if (set == null || set.isEmpty())
            return parent.getService(service);
        return (T) set.iterator().next().getService();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> SortedSet<ServiceDescriptor<T>> getServices(Class<T> service) {
        if (isClosed())
            throw new ScopeClosedException("Cannot get services from a closed scope");
        refreshServices();
        SortedSet<ServiceDescriptor<T>> result = new TreeSet<>(ServiceDescriptor.DESCRIPTOR_COMPARATOR);
        result.addAll((Set<? extends ServiceDescriptor<T>>) descriptors.get(service));
        result.addAll(parent.getServices(service));
        return Collections.unmodifiableSortedSet(result);
    }

    @Override
    public void close() {
        if (!isClosed)
            isClosed = true;
        List<Exception> exceptions = new ArrayList<>();
        for (var set : descriptors.values()) {
            for (var desc : set) {
                if (desc instanceof ServiceDescriptorImpl.Scoped<?> scoped
                        && scoped.getService() instanceof AutoCloseable c) {
                    try {
                        c.close();
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            }
        }
        if (!exceptions.isEmpty())
            throw new ScopeClosingException(exceptions.toArray(Exception[]::new));
    }

    @Override
    public boolean isClosed() {
        return isClosed || (parent instanceof ClosableServiceProvider c && c.isClosed());
    }

    private void refreshServices() {
        var newScopedServiceDescriptors = container.getScopedDescriptors();
        if (Objects.equals(newScopedServiceDescriptors, loadedScopedDescriptors))
            return;
        for (var entry : newScopedServiceDescriptors.entrySet()) {
            var set = descriptors.computeIfAbsent(entry.getKey(), c -> new HashSet<>());
            var descStream = entry.getValue().stream();
            if (loadedScopedDescriptors != null && loadedScopedDescriptors.containsKey(entry.getKey())) {
                var loadedSet = loadedScopedDescriptors.get(entry.getKey());
                descStream = descStream.filter(desc -> !loadedSet.contains(desc));
            }
            descStream.map(desc -> desc.getDescriptor(this))
                    .forEachOrdered(set::add);
        }

        loadedScopedDescriptors = newScopedServiceDescriptors;
    }

}
