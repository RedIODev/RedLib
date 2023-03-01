package dev.redio.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.redio.annotation.PrimitiveClass;

@PrimitiveClass
@SuppressWarnings("preview")
public sealed interface Result<T, E> {

    @PrimitiveClass
    record Ok<T, E>(T value) implements Result<T, E> {
    }

    @PrimitiveClass
    record Err<T, E>(E error) implements Result<T, E> {
    }

    default boolean isOk() {
        return this instanceof Ok<T, E>;
    }

    default boolean isErr() {
        return this instanceof Err<T, E>;
    }

    default <U, F> Result<U, F> map(Function<? super T, ? extends U> okMapper,
            Function<? super E, ? extends F> errMapper) {
        return switch (this) {
            case Ok<T, E>(T value) -> new Ok<>(okMapper.apply(value));
            case Err<T, E>(E error) -> new Err<>(errMapper.apply(error));
        };
    }

    default <U> Result<U,E> mapOk(Function<? super T, ? extends U> mapper) {
        return map(mapper, Function.identity());
    }

    default <F> Result<T,F> mapErr(Function<? super E, ? extends F> mapper) {
        return map(Function.identity(), mapper);
    }

    default T okOr(T other) {
        if (this instanceof Ok<T,E>(T value))
            return value;
        return other;
    }

    default T okOrGet(Supplier<? extends T> generator) {
        if (this instanceof Ok<T,E>(T value)) 
            return value;
        return generator.get();
    }

    default Result<T,E> inspectOk(Consumer<? super T> inspector) {
        if (this instanceof Ok<T,E>(T value))
            inspector.accept(value);
        return this;
    }

    default E errOr(E other) {
        if (this instanceof Err<T,E>(E error))
            return error;
        return other;
    }

    default E errOrGet(Supplier<? extends E> generator) {
        if (this instanceof Err<T,E>(E error))
            return error;
        return generator.get();
    }

    default Result<T,E> inspectErr(Consumer<? super E> inspector) {
        if (this instanceof Err<T,E>(E error))
            inspector.accept(error);
        return this;
    }

    static <T, F, E1 extends F, E2 extends F> Result<T,F> flatten(Result<Result<T,E1>, E2> nestedResult) {
        return switch(nestedResult) {
            case Err<?,E2>(E2 error) -> new Err<>(error);
            case Ok<Result<T,E1>,?>(Ok<T,E1>(T value)) -> new Ok<>(value);
            case Ok<Result<T,E1>,?>(Err<T,E1>(E1 error)) -> new Err<>(error);
        };
    }

    static <T, E> Result<T,E> of(T value) {
        return new Ok<>(value);
    }

    static <T,E> Result<T,E> ofErr(E error) {
        return new Err<>(error);
    }
}
