package dev.redio.concurrent;

import java.util.concurrent.TimeUnit;

public interface Awaitable {

    default void await() throws InterruptedException {
        await(0, null);
    }

    void await(long time, TimeUnit unit) throws InterruptedException;

    boolean cancel();

    
    static void awaitAll(Awaitable... tasks) throws InterruptedException {
        for (var task : tasks)
            task.await();
    }
}
