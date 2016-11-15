package de.hu.flinkydust.data.comparator;

import org.apache.flink.api.java.tuple.Tuple;

/**
 * Komparator, der Tupel annimmt, bei denen der Wert des gewünschten Feldes mindestens den Vergleichswert aufweist.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class AtLeastComparator<T extends Tuple, R extends Comparable<R>> extends TupleFieldComparator<T, R> {

    public AtLeastComparator(int field, R compareValue, Class<R> compareClass) {
        super(field, compareValue, compareClass);
    }

    @Override
    protected boolean evaluate(R value, R compareValue) {
        boolean result;
        /*
         * Wir unboxen die Variablen hier, da wir Float.NaN bzw. Double.NaN
         * als Missing Values interpretieren. Das normale Verhalten von compareTo
         * betrachtet diese Zahlen jedoch als größer als jede beliebige andere Zahl.
         */
        if (value instanceof Float) {
            result = (Float) value >= (Float) compareValue;
        } else if (value instanceof Double) {
            result = (Double) value >= (Double) compareValue;
        } else {
            result = value.compareTo(compareValue) >= 0;
        }

        return result;
    }
}
