package dev.redio.concurrent.task;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import dev.redio.util.Lazy;

public abstract class AbstractTask<T> implements Task<T> {

    public static final int PENDING = 0;
    public static final int CANCELLED = 1;
    public static final int UNCAUGHT_EXCEPTION = 2;
    public static final int READY = 3;
    protected static final Thread.Builder DEFAULT_THREAD_BUILDER = Thread.ofVirtual().name("TaskThread-", 0);

    protected final Thread.Builder builder;
    protected final Thread thread;
    protected volatile int state;
    protected Object data;
    protected final Lazy<Set<LazyTask<? super T, ?>>> lazyTasks = new Lazy<>();

    protected AbstractTask(Thread.Builder threadBuilder) {
        this.builder = Objects.requireNonNull(threadBuilder);
        this.state = 0;
        this.thread = threadBuilder.start(this::runWrapper);
    }

    protected AbstractTask() {
        this(DEFAULT_THREAD_BUILDER);
    }

    /**
     * @implSpec Never throws.
     */
    protected abstract void run();

    @SuppressWarnings("unchecked")
    private void runWrapper() {
        run();
        if (state == PENDING)
            throw new IllegalStateException("Task running after completion");
        var set = lazyTasks.getOrDefault();
        if (set == null)
            return;
        for (var task : set)
            switch (state) {
                case READY -> task.accept((T) data);
                case UNCAUGHT_EXCEPTION, CANCELLED -> task.cancel();
                default -> throw new IllegalStateException("Invalid state: " + state);
            }
            

    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(long time, TimeUnit unit) throws InterruptedException, ExecutionException {
        await(time, unit);
        return switch (state) {
            case PENDING -> throw new IllegalStateException("Task running after completion");
            case CANCELLED -> throw new CancellationException("Task was canceled");
            case UNCAUGHT_EXCEPTION ->
                throw new ExecutionException("Task failed due to an uncaught exception", (Throwable) data);
            case READY -> (T) data;
            default -> throw new IllegalStateException("Invalid state: " + state);
        };
    }

    @Override
    public void await(long time, TimeUnit unit) throws InterruptedException {
        try {
            if (time == 0) {
                thread.join();
                return;
            }
            unit.timedJoin(thread, time);

        } finally {
            if (thread.isInterrupted()) {
                state = CANCELLED;
                throw new InterruptedException("Task was interrupted");
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (state != PENDING)
            return false;
        if (!thread.isAlive())
            return false;
        if (mayInterruptIfRunning)
            thread.interrupt();
        state = CANCELLED;
        return true;

    }

    @Override
    public boolean cancel() {
        if (!thread.isAlive())
            return false;
        thread.interrupt();
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void attach(LazyTask<? super T, ?> lazyTask) {
        switch (state) {
            case PENDING -> lazyTasks.getOrInit(HashSet::new).add(lazyTask);
            case READY -> lazyTask.accept((T)data);
            case UNCAUGHT_EXCEPTION, CANCELLED -> lazyTask.cancel();
        }
    }

    @Override
    public boolean isCancelled() {
        return state == CANCELLED;
    }

    @Override
    public boolean isDone() {
        return state == READY;
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
        return "Task[" + state() + "]";
    }
}
