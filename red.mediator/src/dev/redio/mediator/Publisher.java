package dev.redio.mediator;

import dev.redio.concurrent.task.Task;

@FunctionalInterface
public interface Publisher {

    Task<Void> publish(Notification notification);
}
