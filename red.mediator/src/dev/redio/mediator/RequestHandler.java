package dev.redio.mediator;

import dev.redio.concurrent.task.Task;

@FunctionalInterface
public interface RequestHandler<TRequest extends Request<TResponse>, TResponse> extends BaseHandler {

    Task<TResponse> handle(TRequest request);

}
