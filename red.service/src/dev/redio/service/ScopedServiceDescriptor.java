package dev.redio.service;

public interface ScopedServiceDescriptor<T> {
    
    ServiceDescriptor<T> getDescriptor(ServiceProvider provider);

    Class<T> serviceType();
}
