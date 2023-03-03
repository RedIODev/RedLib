package dev.redio.util.variant;

public interface Var3<V1,V2,V3> extends Variant {

    record T1<V1,V2,V3>(V1 value) implements Var3<V1,V2,V3> {}

    record T2<V1,V2,V3>(V2 value) implements Var3<V1,V2,V3> {}

    record T3<V1,V2,V3>(V3 value) implements Var3<V1,V2,V3> {}
}
