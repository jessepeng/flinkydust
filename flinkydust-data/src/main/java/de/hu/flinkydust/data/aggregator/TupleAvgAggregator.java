package de.hu.flinkydust.data.aggregator;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * Aggregiert mehrere Tupel auf ein Tupel mit dem durchschnittlichen Wert.
 *
 * @param <T>
 *      Klasse der Werte, die aggregiert werden sollen
 * @param <R>
 *     Klasse des Feldes des Tupels, das aggregiert werden soll. Muss das Interface {@link Comparable} implentieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class TupleAvgAggregator<T extends Tuple, R extends Number> extends AggregatorFunction<T, Tuple2<T, Long>>
{
    private Class<R> numberClass;

    private int field;

    public TupleAvgAggregator(int field, Class<R> numberClass) {
        this.field = field;
        this.numberClass = numberClass;
    }

    @Override
    protected Tuple2<T, Long> map(T value) {
        return new Tuple2<>(value, 1L);
    }

    @Override
    protected Tuple2<T, Long> reduce(Tuple2<T, Long> value1, Tuple2<T, Long> value2) {
        Object field1;
        Object field2;
        R number1, number2;
        if ((field1 = value1.getField(field)).getClass().isAssignableFrom(numberClass)) {
            number1 = numberClass.cast(field1);
            if ((field2 = value2.getField(field)).getClass().isAssignableFrom(numberClass)) {
                number2 = numberClass.cast(field2);
            } else {
                throw new ClassCastException("Das Feld " + field + " von Tupel " + value2.toString() + " kann nicht zu Number gecasted werden.");
            }
        } else {
            throw new ClassCastException("Das Feld " + field + " von Tupel " + value1.toString() + " kann nicht zu Number gecasted werden.");
        }

        T newTuple = value1.f0;
        newTuple.setField(number1.longValue() + number2.longValue(), field);

        return new Tuple2<>(newTuple, value1.f1 + value2.f1);
    }

    @Override
    protected T mapBack(Tuple2<T, Long> value) {
        Object fieldValue;
        R number;
        if ((fieldValue = value.getField(field)).getClass().isAssignableFrom(numberClass)) {
            number = numberClass.cast(fieldValue);
            T tuple = value.f0;
            tuple.setField(number.doubleValue() / value.f1, field);
            return tuple;
        } else {
            throw new ClassCastException("Das Feld " + field + " von Tupel " + value.toString() + " kann nicht zu Number gecasted werden.");
        }
    }
}
