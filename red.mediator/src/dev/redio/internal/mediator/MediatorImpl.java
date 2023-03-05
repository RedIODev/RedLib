package dev.redio.internal.mediator;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import dev.redio.concurrent.task.Task;
import dev.redio.mediator.BaseHandler;
import dev.redio.mediator.BaseMessage;
import dev.redio.mediator.Handles;
import dev.redio.mediator.Mediator;
import dev.redio.mediator.Notification;
import dev.redio.mediator.NotificationHandler;
import dev.redio.mediator.Request;
import dev.redio.mediator.RequestHandler;
import dev.redio.service.ServiceDescriptor;
import dev.redio.service.ServiceContainer;
import dev.redio.service.ServiceProvider;

public class MediatorImpl implements Mediator {

    private final ServiceContainer serviceContainer;

    private final ServiceProvider provider;

    public MediatorImpl() {
        var container = ServiceProvider.getDependency(ServiceContainer.class);
        Objects.requireNonNull(container);
        this.serviceContainer = container;
        this.provider = container.getServiceProvider();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TResponse> Task<TResponse> send(Request<TResponse> request) {
        Objects.requireNonNull(request);
        var type = request.getClass();
        var handler = (RequestHandler<Request<TResponse>, TResponse>) getOrRegisterHandler(type);
        if (handler == null)
            throw new IllegalStateException("No handler for Request of type " + type + " found.");
        return handler.handle(request);
    }

    @Override
    public Task<Void> publish(Notification notification) {
        Objects.requireNonNull(notification);
        var type = notification.getClass();
        @SuppressWarnings("unchecked")
        var handler = (NotificationHandler<Notification>) getOrRegisterHandler(type);
        if (handler == null)
            throw new IllegalStateException("No handler for Notification of type " + type + " found.");
        return handler.handle(notification);
    }

    private <T extends BaseHandler> T getOrRegisterHandler(Class<? extends BaseMessage> requestType) {
        Optional<T> handle = tryFindHandler(provider.getServices(BaseHandler.class), requestType);

        if (handle.isPresent())
            return handle.get();
        serviceContainer.addScoped(BaseHandler.class, serviceFilter(requestType));
        return MediatorImpl.<T>tryFindHandler(provider.getServices(BaseHandler.class), requestType).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseHandler> Optional<T> tryFindHandler(
            Set<ServiceDescriptor<BaseHandler>> services, Class<? extends BaseMessage> requestType) {
        return (Optional<T>) services.stream()
                .filter(desc -> serviceFilter(requestType).test(desc.implementationType()))
                .map(ServiceDescriptor::getService)
                .findFirst();
    }

    private static Predicate<Class<? extends BaseHandler>> serviceFilter(Class<? extends BaseMessage> requestType) {
        Predicate<Class<? extends BaseHandler>> filter = desc -> desc.isAnnotationPresent(Handles.class);
        return filter.and(desc -> desc.getAnnotation(Handles.class).value().equals(requestType));
    }
}
