package dev.redio.util;

import java.util.Objects;
import java.util.function.Supplier;

public class Lazy<T> {

    private T value;
    private boolean isInit = false;

    public T getOrSet(T other) {
        if (!isInit)
            init(other);
        return value;
    }

    public T getOrInit(Supplier<? extends T> generator) {
        if (!isInit)
            init(generator.get());
        return value;
    }

    public T getOrDefault() {
        return value;
    }

    public boolean isInit() {
        return isInit;
    }

    public void clear() {
        isInit = false;
        value = null;
    }

    @Override
    public String toString() {
        if (isInit)
            return "Lazy(" + value + ")";
        return "Lazy(uninitialized)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Lazy<?> other))
            return false; 
        if (isInit != other.isInit)
                return false;
        if (!isInit)
            return true;
        return Objects.equals(value, other.value);    
    }

    @Override
    public int hashCode() {
        if (!isInit)
            return -1;
        return Objects.hashCode(value);
    }

    private void init(T other) {
        isInit = true;
        value = other;
    }
}
