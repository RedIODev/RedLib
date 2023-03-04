package dev.redio.util.variant;

public interface Var1<V1> extends Variant {

    record T1<V1>(V1 value) implements Var1<V1> {}

}
