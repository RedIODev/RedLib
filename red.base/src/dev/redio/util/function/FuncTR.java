package dev.redio.util.function;

import java.util.function.Function;

import dev.redio.util.Result;

@FunctionalInterface
public interface FuncTR<IN, OUT, E extends Exception> extends Function<IN, Result<OUT, E>> {

    OUT func(IN param) throws E;

    @SuppressWarnings("unchecked")
    @Override
    default Result<OUT, E> apply(IN t) {
        try {
            return Result.of(func(t));
        } catch (Exception e) {
            if (e instanceof RuntimeException r)
                throw r;
            return Result.ofErr((E) e);
        }
    }

    static <IN, OUT, E extends Exception> Result<OUT,E> apply(FuncTR<IN,OUT,E> func, IN in) {
        return func.apply(in);
    }
}
