package dev.redio.concurrent.task;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConsumeTask<SOURCE> extends AbstractTask<Void> {
    private final Task<SOURCE> source;
    private final Consumer<? super SOURCE> func;

    public ConsumeTask(Task<SOURCE> source, Consumer<? super SOURCE> func) {
        this.source = Objects.requireNonNull(source);
        this.func = Objects.requireNonNull(func);
    }

    @Override
    protected void run() {
        try {
            var previousResult = source.get();
            if (state == CANCELLED)
                return;
            func.accept(previousResult);
            this.state = READY;

        } catch (InterruptedException e) {

        } catch (Throwable t) {
            this.data = t;
            this.state = UNCAUGHT_EXCEPTION;
        }
    }

    @Override
    public <OUT> Task<OUT> fork(Function<? super Void, ? extends OUT> func) {
        return new IntermediateTask<>(this, func);
    }

    @Override
    public Task<Void> fork(Consumer<? super Void> func) {
        return new ConsumeTask<>(this, func);
    }
}
