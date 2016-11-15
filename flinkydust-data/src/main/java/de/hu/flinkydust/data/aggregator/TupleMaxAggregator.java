package de.hu.flinkydust.data.aggregator;

import org.apache.flink.api.java.tuple.Tuple;

/**
 * Aggregiert mehrere Tupel auf den Tupel mit dem größten Wert.
 *
 * @param <T>
 *      Klasse der Werte, die aggregiert werden sollen
 * @param <R>
 *     Klasse des Feldes des Tupels, das aggregiert werden soll. Muss das Interface {@link Comparable} implentieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class TupleMaxAggregator<T extends Tuple, R extends Comparable<R>> extends TupleCompareOneFieldAggregator<T, R> {

    public TupleMaxAggregator(int field, Class<R> comparableClass) {
        super(field, comparableClass);
    }

    @Override
    protected T evaluate(T tuple1, T tuple2, R value1, R value2) {
        boolean left;
        /*
         * Wir unboxen die Variablen hier, da wir Float.NaN bzw. Double.NaN
         * als Missing Values interpretieren. Das normale Verhalten von compareTo
         * betrachtet diese Zahlen jedoch als größer als jede beliebige andere Zahl.
         */
        if (value1 instanceof Float && value2 instanceof Float &&
                (((Float) value1).isNaN() ^ ((Float) value2).isNaN())) {
            left = (!((Float) value1).isNaN() || ((Float) value2).isNaN());
        } else if (value1 instanceof Double && value2 instanceof Double &&
                (((Double) value1).isNaN() ^ ((Double) value2).isNaN())) {
            left = (!((Double) value1).isNaN() || ((Double) value2).isNaN());
        } else {
            left = value1.compareTo(value2) > 0;
        }
        return (left ? tuple1 : tuple2);
    }
}
