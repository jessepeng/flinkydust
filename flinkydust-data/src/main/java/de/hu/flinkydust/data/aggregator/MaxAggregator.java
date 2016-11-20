package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataPoint;

/**
 * Aggregiert mehrere Tupel auf den Tupel mit dem größten Wert.
 *
 * @param <R>
 *     Klasse des Feldes des Tupels, das aggregiert werden soll. Muss das Interface {@link Comparable} implentieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class MaxAggregator<R extends Comparable<R>> extends DataPointCompareOneFieldAggregator<R> {

    public MaxAggregator(String field, R identity, Class<R> comparableClass) {
        super(field, identity, comparableClass);
    }

    @Override
    protected DataPoint evaluate(DataPoint tuple1, DataPoint tuple2, R value1, R value2) {
        return (value1.compareTo(value2) > 0 ? tuple1 : tuple2);
    }
}
