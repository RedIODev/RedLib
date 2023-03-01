package dev.redio.concurrent.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.redio.concurrent.Awaitable;
import dev.redio.concurrent.flow.Flow;
import dev.redio.util.Result;
import dev.redio.util.function.Func;
import dev.redio.util.function.FuncR;
import dev.redio.util.function.FuncTR;
import dev.redio.internal.concurrent.task.CompletedTaskImpl;

public interface Task<T> extends Awaitable, Future<T> {

    default Flow<T> flow() {
        return Flow.of(this);
    }

    @Override
    default T get() throws InterruptedException, ExecutionException {
        return get(0, null);
    }

    @Override
    T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException;

    @Override
    default boolean cancel() {
        return cancel(true);
    }

    void attach(LazyTask<? super T, ?> lazyTask);

    <OUT> Task<OUT> fork(Function<? super T, ? extends OUT> func);

    Task<Void> fork(Consumer<? super T> func);

    static <OUT> Task<OUT> fromResult(OUT value) {
        return new CompletedTaskImpl<>(value);
    }

    static <OUT, E extends Exception> Task<Result<OUT, E>> fromException(E exception) {
        return new CompletedTaskImpl<>(Result.ofErr(exception));
    }

    static Task<Void> startAsync(Runnable r) {
        throw new UnsupportedOperationException();
    }

    static <OUT> Task<OUT> startAsync(Supplier<OUT> s) {
        return new InitTask<>(s);
    }

    static <E extends Exception> Task<Result<Void,E>> startThrowingAsync(Func<E> f) {
        return new InitTask<>(f);
    }

    static <OUT, E extends Exception> Task<Result<OUT,E>> startThrowingAsync(FuncR<OUT,E> f) {
        return new InitTask<>(f);
    }

    static <OUT, IN> Function<IN, Task<OUT>> wrapAsync(Function<? super IN, ? extends OUT> func) {
        return in -> new IntermediateTask<>(Task.fromResult(in), func);
    }

    static <OUT, IN, E extends Exception> Function<IN, Task<Result<OUT, E>>> wrapThrowingAsync(
            FuncTR<? super IN, OUT, E> func) {  //TODO: Revisit OUT bound    
        return in -> new IntermediateTask<>(Task.fromResult(in), func);
    }

    static <OUT> Supplier<Task<OUT>> wrapAsync(Supplier<? extends OUT> func) {
        return () -> new InitTask<>(func);
    }

    static <OUT, E extends Exception> Supplier<Task<Result<OUT, E>>> wrapThrowingAsync(
            FuncR<OUT, E> func) { //TODO: Revisit OUT bound 
        return () -> new InitTask<>(func);
    }
}
