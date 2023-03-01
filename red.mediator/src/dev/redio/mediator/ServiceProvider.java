package dev.redio.mediator;

import dev.redio.internal.mediator.ServiceProviderImpl;

@FunctionalInterface
public interface ServiceProvider {  //move to DIContainer
    
    <T> T getService(Class<?> type);

    static ServiceProvider getDefault() {
        return ServiceProviderImpl.INSTANCE;
    }
}
