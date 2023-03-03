package dev.redio.util.variant;

public interface Var2<V1, V2> extends Variant {

    record T1<V1,V2>(V1 value) implements Var2<V1,V2> {}

    record T2<V1,V2>(V2 value) implements Var2<V1,V2> {}
}
