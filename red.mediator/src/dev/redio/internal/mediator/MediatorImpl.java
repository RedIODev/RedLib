package dev.redio.internal.mediator;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dev.redio.concurrent.task.Task;
import dev.redio.mediator.Mediator;
import dev.redio.mediator.Notification;
import dev.redio.mediator.NotificationHandler;
import dev.redio.mediator.Request;
import dev.redio.mediator.RequestHandler;
import dev.redio.mediator.ServiceProvider;

public class MediatorImpl implements Mediator {

    private final ServiceProvider serviceProvider;
    private static final ConcurrentMap<Class<?>,RequestHandler<?,?>> requestHandlers = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, NotificationHandler<?>> notificationHandlers = new ConcurrentHashMap<>();

    public MediatorImpl(ServiceProvider serviceProvider) {
        this.serviceProvider = Objects.requireNonNull(serviceProvider);
    }

    @Override
    public <TResponse> Task<TResponse> send(Request<TResponse> request) {
        Objects.requireNonNull(request);
        var type = request.getClass();
        @SuppressWarnings("unchecked")
        var handler = (RequestHandler<Request<TResponse>,TResponse>)requestHandlers.computeIfAbsent(type, serviceProvider::getService);
        if (handler == null)
            throw new IllegalStateException("No handler for Request of type " + type + " found.");
        return handler.handle(request);
    }

    @Override
    public Task<Void> publish(Notification notification) {
        Objects.requireNonNull(notification);
        var type = notification.getClass();
        @SuppressWarnings("unchecked")
        var handler = (NotificationHandler<Notification>)notificationHandlers.computeIfAbsent(type, serviceProvider::getService);
        if (handler == null)
            throw new IllegalStateException("No handler for Notification of type " + type + " found.");
        return handler.handle(notification);
    } 
}
