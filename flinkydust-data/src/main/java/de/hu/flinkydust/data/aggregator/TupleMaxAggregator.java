package de.hu.flinkydust.data.aggregator;

import org.apache.flink.api.java.tuple.Tuple;

import java.util.function.BiFunction;

/**
 * Aggregiert mehrere Tupel auf den Tupel mit dem kleinsten Wert.
 *
 * Created by Jan-Christopher on 09.11.2016.
 * @param <T>
 *      Klasse der Werte, die aggregiert werden sollen
 * @param <R>
 *     Klasse des Feldes des Tupels, das aggregiert werden soll. Muss das Interface {@link Comparable} implentieren.
 */
public class TupleMaxAggregator<T extends Tuple, R extends Comparable<R>> implements BiFunction<T, T, T> {

    private int field;

    private Class<R> comparableClass;

    public TupleMaxAggregator(int field, Class<R> comparableClass) {
        this.field = field;
        this.comparableClass = comparableClass;
    }

    @Override
    public T apply(T t, T t2) {
        Object field1;
        Object field2;
        R number1, number2;
        if ((field1 = t.getField(field)).getClass().isAssignableFrom(comparableClass)) {
            number1 = comparableClass.cast(field1);
            if ((field2 = t2.getField(field)).getClass().isAssignableFrom(comparableClass)) {
                number2 = comparableClass.cast(field2);
            } else {
                throw new ClassCastException("Das Feld " + field + " von Tupel " + t2.toString() + " kann nicht zu Comparable gecasted werden.");
            }
        } else {
            throw new ClassCastException("Das Feld " + field + " von Tupel " + t.toString() + " kann nicht zu Comparable gecasted werden.");
        }
        return (number1.compareTo(number2) <= 0 ? t : t2);
    }
}
