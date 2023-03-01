package dev.redio.internal.service;

import java.util.Objects;

import dev.redio.service.ScopedServiceDescriptor;
import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;

public class ScopedServiceDescriptorImpl<T> implements ScopedServiceDescriptor<T> {

    private final Class<T> serviceType;
    private final Class<? extends T> implementationType;

    private ScopedServiceDescriptorImpl(Class<T> serviceType,
            Class<? extends T> implementationType) {
        Objects.requireNonNull(serviceType);
        this.serviceType = serviceType;
        this.implementationType = implementationType;
    }

    @Override
    public ServiceDescriptor<T> getDescriptor(ServiceProvider provider) {
        return (implementationType == null) ? ServiceDescriptorImpl.Scoped.createScoped(provider, serviceType)
                : ServiceDescriptorImpl.Scoped.createScoped(provider, serviceType, implementationType);
    }

    @Override
    public Class<T> serviceType() {
        return serviceType;
    }

    public static <T> ScopedServiceDescriptor<T> createScoped(Class<T> serviceType) {
        return new ScopedServiceDescriptorImpl<>(serviceType, null);
    }

    public static <T> ScopedServiceDescriptor<T> createScoped(Class<T> serviceType,
            Class<? extends T> implementationType) {
        Objects.requireNonNull(implementationType);
        return new ScopedServiceDescriptorImpl<>(serviceType, implementationType);
    }

}
