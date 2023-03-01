package dev.redio.test;

import dev.redio.service.ServiceContainer;

public class TestMain { //TODO: implement equals, hashcode and toString for all classes
    public static void main(String[] args) {
        var container1 = ServiceContainer.defaultContainer();
        var container2 = ServiceContainer.defaultContainer();
        container1.addScoped(SampleClass.class);
        container1.addSingleton(SampleService.class, SampleServiceImpl.class);
        // container1.addSingleton(SampleService.class);
        // container1.addSingleton(SampleClass.class);
        // container2.addSingleton(SampleService.class);
        // container2.addTransient(SampleClass.class);

        var provider1 = container1.getServiceProvider();
        var provider2 = container2.getServiceProvider();

        try (var scope = provider1.createScope()) {
            var provider = scope.serviceProvider();
            var scoped1 = provider.getService(SampleClass.class);
            var scoped2 = provider.getService(SampleClass.class);
            int i = 1;
        }
        // var sc1a = provider1.getService(SampleClass.class);
        // var sc1b = provider1.getService(SampleClass.class);
        
        // var sc2a = provider2.getService(SampleClass.class);
        // var sc2b = provider2.getService(SampleClass.class);
    }
}