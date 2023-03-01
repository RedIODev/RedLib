package dev.redio.test;

import dev.redio.service.ServiceProvider;

public class SampleClass {
    private final SampleService service;

    public SampleClass() {
        this.service = ServiceProvider.getDependency(SampleService.class);
    }
}
