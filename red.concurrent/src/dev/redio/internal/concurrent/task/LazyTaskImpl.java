package dev.redio.internal.concurrent.task;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.redio.concurrent.task.ConsumeTask;
import dev.redio.concurrent.task.IntermediateTask;
import dev.redio.concurrent.task.LazyTask;
import dev.redio.concurrent.task.Task;
import dev.redio.util.Lazy;

import static dev.redio.concurrent.task.AbstractTask.*;

public class LazyTaskImpl<SOURCE, T> implements LazyTask<SOURCE, T> {

    private final Lazy<Set<LazyTask<? super T, ?>>> lazyTasks = new Lazy<>();
    private final Function<? super SOURCE, ? extends T> func;
    private final CountDownLatch awaitLatch = new CountDownLatch(1);
    private int state;
    private Object data;

    public LazyTaskImpl(Task<? extends SOURCE> source, Function<? super SOURCE, ? extends T> func) {
        this.func = Objects.requireNonNull(func);
        source.attach(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        await(timeout, unit);
        return (T) data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void attach(LazyTask<? super T, ?> lazyTask) {
        switch (state) {
            case PENDING -> lazyTasks.getOrInit(HashSet::new).add(lazyTask);
            case READY -> lazyTask.accept((T) data);
            case UNCAUGHT_EXCEPTION, CANCELLED -> lazyTask.cancel();
        }
    }

    @Override
    public void accept(SOURCE source) {
        try {
            if (this.state != PENDING)
                return;
            data = func.apply(source);
            this.state = READY;
        } catch (Throwable t) {
            this.data = t;
            this.state = UNCAUGHT_EXCEPTION;
        } finally {
            awaitLatch.countDown();
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

    @Override
    public void await(long time, TimeUnit unit) throws InterruptedException {
        if (time == 0 && unit == null) {
            awaitLatch.await();
            return;
        }
        awaitLatch.await(time, unit);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.state = CANCELLED;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return this.state == CANCELLED;
    }

    @Override
    public boolean isDone() {
        return this.state == READY;
    }

    @Override
    public State state() {
        return switch (state) {
            case PENDING -> State.RUNNING;
            case CANCELLED -> State.CANCELLED;
            case UNCAUGHT_EXCEPTION -> State.FAILED;
            case READY -> State.SUCCESS;
            default -> throw new IllegalStateException("Invalid state: " + state);
        };
    }

    @Override
    public String toString() {
        return "LazyTask[" + state() + "]";
    }
}
