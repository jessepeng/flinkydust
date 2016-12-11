package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataPoint;

/**
 * Abstrakte Aggregator Klasse, um ein Feld eines Tupels in einer Aggregation zu verwenden.
 * Created by Jan-Christopher on 09.11.2016.
 */
public abstract class DataPointCompareOneFieldAggregator<R extends Comparable<R>> extends ReduceAggregatorFunction<DataPoint> {

    private String field;
    private Class<R> comparableClass;

    public DataPointCompareOneFieldAggregator(String field, Class<R> comparableClass) {
        super(new DataPoint());
        this.comparableClass = comparableClass;
        this.field = field;
    }

    @Override
    public DataPoint reduce(DataPoint value1, DataPoint value2) {
        int index = value1.getFieldIndex(field);
        Object field1;
        Object field2;
        R number1, number2;
        if (!value1.getOptionalValue(index).isPresent()) {
            return value2;
        }
        if (!value2.getOptionalValue(index).isPresent()) {
            return value1;
        }
        if ((field1 = value1.getOptionalValue(index).get()).getClass().isAssignableFrom(comparableClass)) {
            number1 = comparableClass.cast(field1);
            if ((field2 = value2.getOptionalValue(index).get()).getClass().isAssignableFrom(comparableClass)) {
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
    protected abstract DataPoint evaluate(DataPoint tuple1, DataPoint tuple2, R value1, R value2);

}
