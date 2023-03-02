package dev.redio.util.tuple;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public interface Tuple extends Iterable<Object>, Comparable<Tuple>, Serializable {
    // TODO Generate Tuples at compileTime
    // TODO Add Ref to util
    @Override
    default Iterator<Object> iterator() {
        assert getClass().isRecord();
        return new TupleIterator(getClass().getRecordComponents(), (Record & Tuple) this);
    }

    @Override
    default int compareTo(Tuple o) {
        assert getClass().isRecord() && o.getClass().isRecord();
        return getClass().getRecordComponents().length - o.getClass().getRecordComponents().length;
    }

    default Object get(int index) {
        assert getClass().isRecord();
        try {
            return getClass().getRecordComponents()[index].getAccessor().invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not access record component", e);
        }
    }

    default Object get(String fieldName) {
        assert getClass().isRecord();
        try {
            for (var comp : getClass().getRecordComponents())
                if (Objects.equals(fieldName, comp.getName()))
                    return comp.getAccessor().invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not access record component", e);
        }
        throw new NoSuchElementException("No tuple member with name '" + fieldName + "' found");
    }

    default int length() {
        assert getClass().isRecord();
        return getClass().getRecordComponents().length;
    }

    default Object[] toArray() {
        var array = new Object[length()];
        var iter = iterator();
        for (int i = 0; i < array.length; i++)
            array[i] = iter.next();
        return array;
    }

    default List<Object> toList() {
        return List.of(toArray());
    }

    static String toString(Tuple tuple) {
        var result = "(";
        var iter = tuple.iterator();
        while (iter.hasNext()) {
            result += iter.next();
            if (iter.hasNext())
                result += ", ";
        }
        return result + ')';
    }

    final class TupleIterator implements Iterator<Object> {

        private final RecordComponent[] components;

        private final Record record;

        private int index = 0;

        public <RT extends Record & Tuple> TupleIterator(RecordComponent[] components, RT recordTuple) {
            this.components = Objects.requireNonNull(components);
            this.record = Objects.requireNonNull(recordTuple);
        }

        @Override
        public boolean hasNext() {
            return components.length > index;
        }

        @Override
        public Object next() {
            try {
                return components[index++].getAccessor().invoke(record);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Could not access record component", e);
            }
        }
    }
}
