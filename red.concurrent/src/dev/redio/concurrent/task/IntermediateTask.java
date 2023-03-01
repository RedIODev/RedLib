package dev.redio.concurrent.task;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class IntermediateTask<SOURCE,T> extends AbstractTask<T> {

    private final Task<? extends SOURCE> source;
    private final Function<? super SOURCE, ? extends T> func;

    public IntermediateTask(Task<? extends SOURCE> source, Function<? super SOURCE, ? extends T> func) {
        this.source = Objects.requireNonNull(source);
        this.func = Objects.requireNonNull(func);
    }

    @Override
    protected void run() {
        try {
            var previousResult = source.get();
            if (state == CANCELLED)
                return;
            this.data = func.apply(previousResult);
            this.state = READY;

        } catch (InterruptedException e) {
            this.state = CANCELLED;
        } catch (Throwable t) {
            this.data = t;
            this.state = UNCAUGHT_EXCEPTION;
        }
    }

    @Override
    public <OUT> Task<OUT> fork(Function<? super T, ? extends OUT> func) {
        return new IntermediateTask<>(this, func);
    }

    @Override
    public Task<Void> fork(Consumer<? super T> func) {
        return new ConsumeTask<>(this, func);
    }
}
