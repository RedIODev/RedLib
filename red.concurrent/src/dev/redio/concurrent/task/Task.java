package dev.redio.concurrent.task;

public interface Task<T> {
    
    Poll<T> poll(Context context);

    default T await() {
        throw new IllegalAwaitError("Await called outside async method");
    }

    static <T> T block(Task<T> task) {
        
    }
}
