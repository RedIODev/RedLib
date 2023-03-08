package dev.redio.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.redio.annotation.PrimitiveClass;

@PrimitiveClass
public sealed interface Result<T, E> {

    @PrimitiveClass
    record Ok<T, E>(T value) implements Result<T, E> {
    }

    @PrimitiveClass
    record Err<T, E>(E error) implements Result<T, E> {
    }

    default boolean isOk() {
        return this instanceof Ok;
    }

    default boolean isErr() {
        return this instanceof Err;
    }

    default <U, F> Result<U, F> map(Function<? super T, ? extends U> okMapper,
            Function<? super E, ? extends F> errMapper) {
        return switch (this) {
            case Ok<T, E> ok -> new Ok<>(okMapper.apply(ok.value));
            case Err<T, E> err -> new Err<>(errMapper.apply(err.error));
        };
    }

    default <U> Result<U,E> mapOk(Function<? super T, ? extends U> mapper) {
        return map(mapper, Function.identity());
    }

    default <F> Result<T,F> mapErr(Function<? super E, ? extends F> mapper) {
        return map(Function.identity(), mapper);
    }

    default T okOr(T other) {
        if (this instanceof Ok<T,E> ok)
            return ok.value;
        return other;
    }

    default T okOrGet(Supplier<? extends T> generator) {
        if (this instanceof Ok<T,E> ok) 
            return ok.value;
        return generator.get();
    }

    default Result<T,E> inspectOk(Consumer<? super T> inspector) {
        if (this instanceof Ok<T,E> ok)
            inspector.accept(ok.value);
        return this;
    }

    default E errOr(E other) {
        if (this instanceof Err<T,E> err)
            return err.error;
        return other;
    }

    default E errOrGet(Supplier<? extends E> generator) {
        if (this instanceof Err<T,E> err)
            return err.error;
        return generator.get();
    }

    default Result<T,E> inspectErr(Consumer<? super E> inspector) {
        if (this instanceof Err<T,E> err)
            inspector.accept(err.error);
        return this;
    }

    static <T, F, E1 extends F, E2 extends F> Result<T,F> flatten(Result<Result<T,E1>, E2> nestedResult) {
        return switch(nestedResult) {
            case Err<?,E2> err -> new Err<>(err.error);
            case Ok<Result<T,E1>,?> ok -> switch (ok.value) {
                case Ok<T,?> okInner -> new Ok<>(okInner.value);
                case Err<?,E1> errInner -> new Err<>(errInner.error);
            };
        };
    }

    static <T, E> Result<T,E> of(T value) {
        return new Ok<>(value);
    }

    static <T,E> Result<T,E> ofErr(E error) {
        return new Err<>(error);
    }
}
