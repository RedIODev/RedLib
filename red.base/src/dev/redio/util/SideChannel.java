package dev.redio.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.redio.annotation.ValueClass;

@ValueClass
public final class SideChannel<T> implements AutoCloseable {

    private static final ThreadLocal<Map<Class<?>, Object>> INSTANCE_STORE = ThreadLocal.withInitial(HashMap::new);
    private final Class<? extends T> type;

    private SideChannel(T value, Class<T> type) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(type);
        this.type = type;
        INSTANCE_STORE.get().put(type, value);
        
    }

    public static <T> SideChannel<T> register(T value, Class<T> type) {
        return new SideChannel<>(value, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T acquire(Class<T> type) {
        Objects.requireNonNull(type);
        if (!INSTANCE_STORE.get().containsKey(type))
            return null;
        return (T) INSTANCE_STORE.get().get(type);
    }

    @Override
    public void close() {
        INSTANCE_STORE.get().remove(type);
    }
}
