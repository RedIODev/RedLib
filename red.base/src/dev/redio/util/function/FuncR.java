package dev.redio.util.function;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import dev.redio.util.Result;

@FunctionalInterface
public interface FuncR<OUT, E extends Exception> extends Supplier<Result<OUT, E>> {

    OUT func() throws E;

    @SuppressWarnings("unchecked")
    @Override
    default Result<OUT, E> get() {
        try {
            return Result.of(func());
        } catch (Exception e) {
            if (e instanceof RuntimeException r)
                throw r;
            return Result.ofErr((E) e);
        }
    }

    static <OUT> FuncR<OUT,Exception> ofCallable(Callable<OUT> c) {
        return c::call;
    }

    static <OUT, E extends Exception> Result<OUT,E> get(FuncR<OUT,E> func) {
        return func.get();
    }
}
