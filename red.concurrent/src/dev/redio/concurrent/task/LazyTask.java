package dev.redio.concurrent.task;

import java.util.function.Consumer;

public interface LazyTask<SOURCE,T> extends Task<T>, Consumer<SOURCE> {

    @Override
    void accept(SOURCE t);
}
