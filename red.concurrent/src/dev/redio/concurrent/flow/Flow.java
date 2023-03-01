package dev.redio.concurrent.flow;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.redio.concurrent.task.Task;
import dev.redio.internal.concurrent.flow.FlowImpl;

public interface Flow<T> {

    <OUT> Flow<OUT> map(Function<? super T, ? extends OUT> func);

    <OUT> Flow<OUT> mapAsync(Function<? super T, ? extends Task<? extends OUT>> func);

    Flow<Void> consume(Consumer<? super T> func);

    Flow<Void> consumeAsync(Function<? super T, Task<Void>> func);

    Task<T> collect();

    static <OUT> Flow<OUT> of(Task<OUT> task) {
        return new FlowImpl<>(Objects.requireNonNull(task));
    }

}
