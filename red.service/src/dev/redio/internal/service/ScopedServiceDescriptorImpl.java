package dev.redio.internal.service;

import java.util.Objects;
import java.util.function.Predicate;

import dev.redio.service.ScopedServiceDescriptor;
import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;

public abstract class ScopedServiceDescriptorImpl<T> implements ScopedServiceDescriptor<T> {

    protected final Class<T> serviceType;
   

    protected ScopedServiceDescriptorImpl(Class<T> serviceType) {
        Objects.requireNonNull(serviceType);
        this.serviceType = serviceType;
    }

    @Override
    public Class<T> serviceType() {
        return serviceType;
    }

    private static class Implementation<T> extends ScopedServiceDescriptorImpl<T> {

        private final Class<? extends T> implementationType;

        protected Implementation(Class<T> serviceType, Class<? extends T> implementationType) {
            super(serviceType);
            Objects.requireNonNull(implementationType);
            this.implementationType = implementationType;
        }

        @Override
        public ServiceDescriptor<T> getDescriptor(ServiceProvider provider) {
            return ServiceDescriptorImpl.Scoped.createScoped(provider, serviceType, implementationType).orElse(null);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Implementation<?> other))
                return false;
            if (!Objects.equals(serviceType, other.serviceType))
                return false;
            return Objects.equals(implementationType, other.implementationType);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(serviceType, implementationType);
        }
    }

    private static class Filter<T> extends ScopedServiceDescriptorImpl<T> {

        private final Predicate<Class<? extends T>> filter;

        protected Filter(Class<T> serviceType, Predicate<Class<? extends T>> filter) {
            super(serviceType);
            Objects.requireNonNull(filter);
            this.filter = filter;
        }

        @Override
        public ServiceDescriptor<T> getDescriptor(ServiceProvider provider) {
            return ServiceDescriptorImpl.Scoped.createScoped(provider, serviceType, filter).orElse(null);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Filter<?> other))
                return false;
            if (!Objects.equals(serviceType, other.serviceType))
                return false;
            return Objects.equals(filter, other.filter);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(serviceType, filter);
        }
    }

    private static class Service<T> extends ScopedServiceDescriptorImpl<T> {

        protected Service(Class<T> serviceType) {
            super(serviceType);
        }

        @Override
        public ServiceDescriptor<T> getDescriptor(ServiceProvider provider) {
            return ServiceDescriptorImpl.Scoped.createScoped(provider, serviceType).orElse(null);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Service<?> other))
                return false;
            return Objects.equals(serviceType, other.serviceType);
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(serviceType);
        }
    }

    public static <T> ScopedServiceDescriptor<T> createScoped(Class<T> serviceType) {
        return new Service<>(serviceType);
    }

    public static <T> ScopedServiceDescriptor<T> createScoped(Class<T> serviceType,
            Class<? extends T> implementationType) {
        Objects.requireNonNull(implementationType);
        return new Implementation<>(serviceType, implementationType);
    }

    public static <T> ScopedServiceDescriptor<T> createScoped(Class<T> serviceType, Predicate<Class<? extends T>> filter) {
        Objects.requireNonNull(filter);
        return new Filter<>(serviceType, filter);
    }

}
