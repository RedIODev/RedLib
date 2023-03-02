package dev.redio.internal.service;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Predicate;

import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceProvider;
import dev.redio.util.SideChannel;
import dev.redio.util.tuple.Tup2;

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

    private static <T> Optional<Provider<T>> findProvider(Class<T> serviceType, Predicate<Class<? extends T>> filter) {
        return ServiceLoader.load(serviceType).stream().filter(p -> filter.test(p.type())).findFirst();
    }

    private static <T> Optional<Tup2<Class<? extends T>, T>> createInstance(ServiceProvider provider,
            Class<T> serviceType, Class<? extends T> implementationType) {
        return createInstance(provider, serviceType, t -> t.equals(implementationType));
    }

    private static <T> Optional<Tup2<Class<? extends T>, T>> createInstance(ServiceProvider provider,
            Class<T> serviceType, Predicate<Class<? extends T>> filter) {
        try (var channel = SideChannel.register(provider, ServiceProvider.class)) {
            return findProvider(serviceType, filter)
                    .map(p -> new Tup2<>(p.type(), p.get()));
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

        public static <T> Optional<Singleton<T>> createSingleton(ServiceProvider provider, Class<T> serviceType) {
            return createSingleton(provider, serviceType, serviceType);
        }

        public static <T> Optional<Singleton<T>> createSingleton(ServiceProvider provider, Class<T> serviceType,
                Class<? extends T> implementationType) {
            return createInstance(provider, serviceType, implementationType)
                    .map(t -> new Singleton<>(serviceType, implementationType, t.item2()));
        }

        public static<T> Optional<Singleton<T>> createSingleton(ServiceProvider provider, Class<T> serviceType, Predicate<Class<? extends T>> filter) {
            return createInstance(provider, serviceType, filter)
                    .map(t -> new Singleton<>(serviceType, t.item1(), t.item2()));
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

        public static <T> Optional<Scoped<T>> createScoped(ServiceProvider provider, Class<T> serviceType) {
            return createScoped(provider, serviceType, serviceType);
        }

        public static <T> Optional<Scoped<T>> createScoped(ServiceProvider provider, Class<T> serviceType,
                Class<? extends T> implementationType) {
            return createInstance(provider, serviceType, implementationType)
                    .map(t -> new Scoped<>(serviceType, implementationType, t.item2()));
        }

        public static <T> Optional<Scoped<T>> createScoped(ServiceProvider provider, Class<T> serviceType,
                Predicate<Class<? extends T>> filter) {
            return createInstance(provider, serviceType, filter)
                    .map(t -> new Scoped<>(serviceType, t.item1(), t.item2()));
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
            Objects.requireNonNull(provider);
            Objects.requireNonNull(serviceType);
            this.serviceProvider = serviceProvider;
            this.provider = provider;
        }

        public static <T> Optional<Transient<T>> createTransient(ServiceProvider provider, Class<T> serviceType) {
            return createTransient(provider, serviceType, serviceType);
        }

        public static <T> Optional<Transient<T>> createTransient(ServiceProvider provider, Class<T> serviceType,
                Class<? extends T> implementationType) {
            Objects.requireNonNull(implementationType);
            return findProvider(serviceType, t -> t.equals(implementationType))
                    .map(p -> new Transient<>(provider, serviceType, p));
        }

        public static <T> Optional<Transient<T>> createTransient(ServiceProvider provider, Class<T> serviceType, Predicate<Class<? extends T>> filter) {
            Objects.requireNonNull(filter);
            return findProvider(serviceType, filter)
                    .map(p -> new Transient<>(provider, serviceType, p));
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
}
