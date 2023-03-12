package dev.redio.concurrent.test;

import dev.redio.concurrent.Async;
import dev.redio.concurrent.task.Task;

public class AsyncTest {

    @Async
    public static Task<String> testAsync() {
        var string = System.console().readLine();
        waitAsync(2000).await();
        return Task.completed(string);
    }

    public static Task<Void> waitAsync(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Task.completed(null);
    }
}
