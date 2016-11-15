package de.hu.flinkydust.data.aggregator;

import org.apache.flink.api.java.tuple.Tuple;

/**
 * Abstrakte Aggregator Klasse, um ein Feld eines Tupels in einer Aggregation zu verwenden.
 * Created by Jan-Christopher on 09.11.2016.
 */
public abstract class TupleCompareOneFieldAggregator<T extends Tuple, R extends Comparable<R>> extends ReduceAggregatorFunction<T> {

    private int field;
    private Class<R> comparableClass;

    public TupleCompareOneFieldAggregator(int field, Class<R> comparableClass) {
        this.comparableClass = comparableClass;
        this.field = field;
    }

    @Override
    public T reduce(T value1, T value2) {
        Object field1;
        Object field2;
        R number1, number2;
        if ((field1 = value1.getField(field)).getClass().isAssignableFrom(comparableClass)) {
            number1 = comparableClass.cast(field1);
            if ((field2 = value2.getField(field)).getClass().isAssignableFrom(comparableClass)) {
                number2 = comparableClass.cast(field2);
            } else {
                throw new ClassCastException("Das Feld " + field + " von Tupel " + value2.toString() + " kann nicht zu Comparable gecasted werden.");
            }
        } else {
            throw new ClassCastException("Das Feld " + field + " von Tupel " + value1.toString() + " kann nicht zu Comparable gecasted werden.");
        }
        return evaluate(value1, value2, number1, number2);
    }

    /**
     * Funktion, die die beiden Tupel auswertet und einen Wert zurückgibt.
     * @param tuple1
     *          Erster Tupel
     * @param tuple2
     *          Zweiter Tupel
     * @param value1
     *          Vergleichbarer Wert des ersten Tupels
     * @param value2
     *          Vergleichbarer Wert des zweiten Tupels
     * @return
     *          Das Tupel, das den Vergleich "gewonnen" hat
     */
    protected abstract T evaluate(T tuple1, T tuple2, R value1, R value2);

}
