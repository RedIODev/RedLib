package dev.redio.concurrent.task;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class InitTask<T> extends AbstractTask<T> {

    private final Supplier<? extends T> func;
    public InitTask(Supplier<? extends T> func) {
        this.func = Objects.requireNonNull(func);
    }

    @Override
    protected void run() {
        try {
            this.data = func.get();
            this.state = READY;
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
