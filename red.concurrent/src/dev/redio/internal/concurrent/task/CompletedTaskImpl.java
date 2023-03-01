package dev.redio.internal.concurrent.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.redio.concurrent.task.LazyTask;
import dev.redio.concurrent.task.Task;

public class CompletedTaskImpl<T> implements Task<T> {

    private final T data;

    public CompletedTaskImpl(T data) {
        this.data = data;
    }

    @Override
    public void await(long time, TimeUnit unit) throws InterruptedException {}

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
       return data;
    }

    @Override
    public void attach(LazyTask<? super T, ?> lazyTask) {
        lazyTask.accept(data);
    }

    @Override
    public <OUT> Task<OUT> fork(Function<? super T, ? extends OUT> func) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Task<Void> fork(Consumer<? super T> func) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
