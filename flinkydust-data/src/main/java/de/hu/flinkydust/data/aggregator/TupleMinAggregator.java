package de.hu.flinkydust.data.aggregator;

import org.apache.flink.api.java.tuple.Tuple;

/**
 * Aggregiert mehrere Tupel auf den Tupel mit dem kleinsten Wert.
 *
 * @param <T>
 *      Klasse der Werte, die aggregiert werden sollen
 * @param <R>
 *     Klasse des Feldes des Tupels, das aggregiert werden soll. Muss das Interface {@link Comparable} implentieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class TupleMinAggregator<T extends Tuple, R extends Comparable<R>> extends TupleCompareOneFieldAggregator<T, R> {

    public TupleMinAggregator(int field, Class<R> comparableClass) {
        super(field, comparableClass);
    }

    @Override
    protected T evaluate(T tuple1, T tuple2, R value1, R value2) {
        return (value1.compareTo(value2) <= 0 ? tuple1 : tuple2);
    }

}
