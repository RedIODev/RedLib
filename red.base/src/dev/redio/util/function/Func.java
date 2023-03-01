package dev.redio.util.function;

import java.util.function.Supplier;

import dev.redio.util.Result;

@FunctionalInterface
public interface Func<E extends Exception> extends Supplier<Result<Void, E>> {  

    void func() throws E;

    @SuppressWarnings("unchecked")
    @Override
    default Result<Void, E> get() {
        try {
            func();
            return Result.of(null);
        } catch (Exception e) {
            if (e instanceof RuntimeException r)
                throw r;
            return Result.ofErr((E) e);
        }
    }

    static <E extends Exception> Result<Void,E> run(Func<E> func) {
        return func.get();
    }
}

// class Main {
//     void test() {
//         Foo<?> subC = null;
//         bar(subC, subC);
//     }

//     <A> void bar(Foo<A> a, Foo<? extends A> ax) {

//     }
// }

// class Foo<A> {
// }
