package dev.redio.util.function;

import java.util.function.Function;

import dev.redio.util.Result;
@FunctionalInterface
public interface FuncT<IN, E extends Exception> extends Function<IN, Result<Void, E>> {

    void func(IN param) throws E;

    @SuppressWarnings("unchecked")
    @Override
    default Result<Void, E> apply(IN t) {
        try {
            func(t);
            return Result.of(null);
        } catch (Exception e) {
            if (e instanceof RuntimeException r)
                throw r;
            return Result.ofErr((E) e);
        }
    }

    static <IN, E extends Exception> Result<Void, E> apply(FuncT<IN,E> func, IN in) {
        return func.apply(in);
    }
}
