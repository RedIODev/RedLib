package dev.redio.internal.mediator;

import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import dev.redio.mediator.BaseHandler;
import dev.redio.mediator.Handles;
import dev.redio.mediator.ServiceProvider;

public enum ServiceProviderImpl implements ServiceProvider {
    INSTANCE;

    private final ServiceLoader<BaseHandler> loader = ServiceLoader.load(BaseHandler.class);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<?> type) {
        loader.reload();
        return (T) loader.stream()
                .filter(h -> h.type().isAnnotationPresent(Handles.class))
                .filter(h -> h.type().getAnnotation(Handles.class).value().equals(type))
                .map(Provider::get)
                .findFirst()
                .orElse(null);
    }

}
