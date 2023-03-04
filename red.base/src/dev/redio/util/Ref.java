package dev.redio.util;

import java.util.Objects;

public class Ref<T> {
    public T value;

    public Ref() {
        this(null);
    }

    public Ref(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Ref<?> other))
            return false;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "&" + value;
    }
}
