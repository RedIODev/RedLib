package dev.redio.concurrent.task;

import java.util.function.Function;

import dev.redio.annotation.PrimitiveClass;

@PrimitiveClass
public sealed interface Poll<T> {
    
    @PrimitiveClass
    record Ready<T>(T value) implements Poll<T> {}

    @PrimitiveClass
    record Pending<T>() implements Poll<T> {}

    default <U> Poll<U> map(Function<T,U> f) {
        return switch (this) {
            case Ready<T> ready -> new Ready<>(f.apply(ready.value));
            case Pending<T> pending -> new Pending<>();
        };
    }

    default boolean isReady() {
        return this instanceof Ready;
    }

    default boolean isPending() {
        return this instanceof Pending;
    }
}
