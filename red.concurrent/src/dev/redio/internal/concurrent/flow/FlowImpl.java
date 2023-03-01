package dev.redio.internal.concurrent.flow;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.redio.concurrent.task.Task;
import dev.redio.concurrent.flow.Flow;
import dev.redio.internal.concurrent.task.LazyTaskImpl;

public class FlowImpl<T> implements Flow<T> {

    private final Task<T> task;

    public FlowImpl(Task<T> task) {
        this.task = task;
    }

   
    @Override
    public <U> Flow<U> map(Function<? super T, ? extends U> func) {
        return new LazyTaskImpl<T,U>(this.task, func).flow();
    }

    @Override
    public <U> Flow<U> mapAsync(Function<? super T, ? extends Task<? extends U>> func) {
        return new LazyTaskImpl<T,U>(this.task, t -> flatten(t, func)).flow();
    }

    @Override
    public Flow<Void> consume(Consumer<? super T> func) {
        return task.fork(func).flow();
    }

    @Override
    public Flow<Void> consumeAsync(Function<? super T, Task<Void>> func) {
        return new LazyTaskImpl<T,Void>(this.task, t -> flatten(t, func)).flow();
    }

    @Override
    public Task<T> collect() {
        return task;
    }

    private static <U,T> U flatten(T source, Function<? super T, ? extends Task<? extends U>> func) {
        try {
            return func.apply(source).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
