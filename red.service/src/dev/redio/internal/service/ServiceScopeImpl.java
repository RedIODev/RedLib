package dev.redio.internal.service;

import dev.redio.service.ServiceProvider;
import dev.redio.service.ServiceScope;

public record ServiceScopeImpl(ServiceProvider serviceProvider) implements ServiceScope {
}
