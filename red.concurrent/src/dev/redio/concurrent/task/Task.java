package dev.redio.concurrent.task;

import dev.redio.internal.concurrent.TaskCompleted;

public interface Task<T> {
    
    Poll<T> poll(Context context);

    default T await() {
        throw new IllegalAwaitError("Await called outside async method");
    }

    static <T> T block(Task<T> task) {
        return null;
    }

    static <T> Task<T> completed(T value) {
        return new TaskCompleted<>(value);
    }
}
