package dev.redio.mediator;

import dev.redio.internal.mediator.MediatorImpl;
import dev.redio.service.ServiceContainer;

public interface Mediator extends Sender, Publisher {

    static void registerDefaultMediator(ServiceContainer container) {
        container.addSingleton(Mediator.class, MediatorImpl.class);
    }
}
