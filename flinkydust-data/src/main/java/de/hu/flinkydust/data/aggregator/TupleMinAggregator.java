package de.hu.flinkydust.data.aggregator;

import org.apache.flink.api.java.tuple.Tuple;

import java.util.function.BiFunction;

/**
 * Created by Jan-Christopher on 09.11.2016.
 */
public class TupleMinAggregator<T extends Tuple> implements BiFunction<T, T, T> {
    @Override
    public T apply(T t, T t2) {
        return null;
    }
}
