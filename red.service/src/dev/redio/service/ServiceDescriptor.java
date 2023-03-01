package dev.redio.service;

import java.util.Comparator;

public interface ServiceDescriptor<T> {

    static int TRANSIENT_PRIORITY = 0;
    static int SINGLETON_PRIORITY = 1;
    static int SCOPED_PRIORITY = 2;

    static Comparator<ServiceDescriptor<?>> DESCRIPTOR_COMPARATOR = Comparator.<ServiceDescriptor<?>>comparingInt(ServiceDescriptor::priority).reversed();
    
    Class<? extends T> implementationType();

    Class<T> serviceType();

    T getService();

    int priority();

}
