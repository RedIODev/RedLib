package dev.redio.internal.service;

import java.util.Objects;
import java.util.ServiceLoader.Provider;

import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;
import dev.redio.util.SideChannel;

public abstract class ServiceDescriptorImpl<T> implements ServiceDescriptor<T> {

    private final Class<T> serviceType;
    private final Class<? extends T> implementationType;

    protected ServiceDescriptorImpl(Class<T> serviceType, Class<? extends T> implementationType) {
        Objects.requireNonNull(serviceType);
        Objects.requireNonNull(implementationType);
        this.serviceType = serviceType;
        this.implementationType = implementationType;
    }

    @Override
    public Class<T> serviceType() {
        return serviceType;
    }

    @Override
    public Class<? extends T> implementationType() {
        return implementationType;
    }

    private static <T> T createInstance(ServiceProvider loader, Class<T> serviceType) {
        try (var channel = SideChannel.register(loader, ServiceProvider.class)) {
            return java.util.ServiceLoader.load(serviceType).findFirst().orElse(null);
        }
    }

    private static <T> T createInstance(ServiceProvider loader, Class<T> serviceType,
            Class<? extends T> implementationType) {
        try (var channel = SideChannel.register(loader, ServiceProvider.class)) {
            return java.util.ServiceLoader.load(serviceType)
                    .stream()
                    .filter(p -> p.type().equals(implementationType))
                    .map(java.util.ServiceLoader.Provider::get)
                    .findFirst()
                    .get();
        }
    }

    public static class Singleton<T> extends ServiceDescriptorImpl<T> {

        private final T instance;

        protected Singleton(Class<T> serviceType, Class<? extends T> implementationType, T instance) {
            super(serviceType, implementationType);
            Objects.requireNonNull(instance);
            this.instance = instance;
        }

        @SuppressWarnings("unchecked")
        public static <T> Singleton<T> createSingleton(Class<T> serviceType, T instance) {
            return new Singleton<>(serviceType, (Class<? extends T>) instance.getClass(), instance);
        }

        @SuppressWarnings("unchecked")
        public static <T> Singleton<T> createSingleton(ServiceProvider loader, Class<T> serviceType) {
            Objects.requireNonNull(loader);
            T instance = createInstance(loader, serviceType);
            if (instance == null)
                return null;
            return new Singleton<>(serviceType, (Class<? extends T>) instance.getClass(), instance);
        }

        public static <T> Singleton<T> createSingleton(ServiceProvider loader, Class<T> serviceType,
                Class<? extends T> implementationType) {
            T instance = createInstance(loader, serviceType, implementationType);
            if (instance == null)
                return null;
            return new Singleton<>(serviceType, implementationType, instance);
        }

        @Override
        public T getService() {
            return instance;
        }

        @Override
        public int priority() {
            return SINGLETON_PRIORITY;
        }
    }

    public static class Scoped<T> extends Singleton<T> {

        protected Scoped(Class<T> serviceType, Class<? extends T> implementationType, T instance) {
            super(serviceType, implementationType, instance);
        }

        @SuppressWarnings("unchecked")
        public static <T> Scoped<T> createScoped(ServiceProvider loader, Class<T> serviceType) {
            Objects.requireNonNull(loader);
            T instance = createInstance(loader, serviceType);
            if (instance == null)
                return null;
            return new Scoped<>(serviceType, (Class<? extends T>) instance.getClass(), instance);
        }

        public static <T> Scoped<T> createScoped(ServiceProvider loader, Class<T> serviceType,
                Class<? extends T> implementationType) {
            T instance = createInstance(loader, serviceType, implementationType);
            if (instance == null)
                return null;
            return new Scoped<>(serviceType, implementationType, instance);
        }

        @Override
        public int priority() {
            return SCOPED_PRIORITY;
        }

    }

    public static class Transient<T> extends ServiceDescriptorImpl<T> {

        private final ServiceProvider serviceProvider;
        private final Provider<T> provider;

        private Transient(ServiceProvider serviceProvider, Class<T> serviceType, Provider<T> provider) {
            super(serviceType, provider.type());
            this.serviceProvider = serviceProvider;
            this.provider = provider;
        }

        public static <T> Transient<T> createTransient(ServiceProvider loader, Class<T> serviceType) {
            Objects.requireNonNull(loader);
            Objects.requireNonNull(serviceType);
            var provider = java.util.ServiceLoader.load(serviceType).stream().findFirst().get();
            if (provider == null)
                return null;
            return new Transient<>(loader, serviceType, provider);
        }

        public static <T> Transient<T> createTransient(ServiceProvider loader, Class<T> serviceType,
                Class<? extends T> implementationType) {
            Objects.requireNonNull(loader);
            Objects.requireNonNull(serviceType);
            Objects.requireNonNull(implementationType);
            var provider = java.util.ServiceLoader.load(serviceType)
                    .stream()
                    .filter(p -> p.type().equals(implementationType))
                    .findFirst()
                    .get();
            if (provider == null)
                return null;
            return new Transient<>(loader, serviceType, provider);
        }

        @Override
        public T getService() {
            try (var channel = SideChannel.register(serviceProvider, ServiceProvider.class)) {
                return provider.get();
            }
        }

        @Override
        public int priority() {
            return TRANSIENT_PRIORITY;
        }
    }

    // @Override
    // public T getService() {
    // try (var channel = SideChannel.register(serviceProvider)) {
    // return provider.get();
    // }
    // }

    // @Override
    // public RegistrationType registrationType() {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'registrationType'");
    // }

}
