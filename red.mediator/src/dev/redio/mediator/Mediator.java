package dev.redio.mediator;

import dev.redio.internal.mediator.MediatorImpl;

public interface Mediator extends Sender, Publisher {    //TODO: change to dev.redio.concurrent.Task

    static Mediator newDefault(ServiceProvider serviceProvider) {
        return new MediatorImpl(serviceProvider);
    }


}
