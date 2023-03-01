package dev.redio.mediator;

import dev.redio.concurrent.task.Task;

@FunctionalInterface
public interface NotificationHandler<TNotification extends Notification> extends BaseHandler {
    
    Task<Void> handle(TNotification notification);
}
