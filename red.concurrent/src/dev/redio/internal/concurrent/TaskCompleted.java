package dev.redio.internal.concurrent;

import dev.redio.concurrent.task.Context;
import dev.redio.concurrent.task.Poll;
import dev.redio.concurrent.task.Task;

public class TaskCompleted<T> extends StateMachine implements Task<T> {

    private final T value;

    public TaskCompleted(T value) {
        this.value = value;
    }

    @Override
    public Poll<T> poll(Context context) {
        return new Poll.Ready<>(value);
    }

}
