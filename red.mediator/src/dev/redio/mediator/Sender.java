package dev.redio.mediator;

import dev.redio.concurrent.task.Task;

@FunctionalInterface
public interface Sender {

    <TResponse> Task<TResponse> send(Request<TResponse> request);
}
