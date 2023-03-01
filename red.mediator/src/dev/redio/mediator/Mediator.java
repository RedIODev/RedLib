package dev.redio.mediator;

import dev.redio.internal.mediator.MediatorImpl;
import dev.redio.service.ServiceContainer;

public interface Mediator extends Sender, Publisher, AutoCloseable {

    static void registerDefaultMediator(ServiceContainer container) {//TODO: Rethink MediatorScope and Singletonness of Mediator
        container.addSingleton(Mediator.class, MediatorImpl.class);
    }
}
