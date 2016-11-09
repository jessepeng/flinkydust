package de.hu.flinkydust.data.comparator;

import org.apache.flink.api.java.tuple.Tuple;

import java.util.function.Predicate;

/**
 * Created by Jan-Christopher on 09.11.2016.
 */
public abstract class TupleFieldComparator<T extends Tuple> implements Predicate<T> {
}
