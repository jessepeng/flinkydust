package de.hu.flinkydust.data.comparator;

import org.apache.flink.api.java.tuple.Tuple;

/**
 * Komparator, der Tupel annimmt, bei denen der Wert des gewünschten Feldes mit dem Vergleichswert übereinstimmt.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class AtLeastComparator<T extends Tuple, R extends Comparable<R>> extends TupleFieldComparator<T, R> {

    public AtLeastComparator(int field, R compareValue, Class<R> compareClass) {
        super(field, compareValue, compareClass);
    }

    @Override
    protected boolean evaluate(R value, R compareValue) {
        return value.compareTo(compareValue) == 0;
    }
}
