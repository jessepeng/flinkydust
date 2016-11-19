package de.hu.flinkydust.data.comparator;

import de.hu.flinkydust.data.DataPoint;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple;

import java.util.function.Predicate;

/**
 * Abstrakte Basis-Klasse, um Komparator-Funktionen auf einem Feld eines Datensatzes zu implementieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public abstract class DataPointComparator<R extends Comparable<R>> implements Predicate<DataPoint> {

    private String field;
    private R compareValue;
    private R missingValue;
    private Class<R> compareClass;

    public DataPointComparator(String field, R compareValue, R missingValue, Class<R> compareClass) {
        this.field = field;
        this.compareValue = compareValue;
        this.missingValue = missingValue;
        this.compareClass = compareClass;
    }

    @Override
    public boolean test(DataPoint t) {
        Object tupleValue;
        if ((tupleValue = t.getOptionalValue(t.getFieldIndex(field)).orElse(missingValue)).getClass().isAssignableFrom(compareClass)) {
            R value = compareClass.cast(tupleValue);
            return (evaluate(value, compareValue));
        }
        throw new ClassCastException("Das Feld " + field + " von Tupel " + t.toString() + " kann nicht zu Comparable gecasted werden.");
    }

    /**
     * Evaluiert den Wert gegenüber einem Vergleichswert und gibt zurück, ob das Tupel gewählt werden soll oder nicht.
     * @param value
     *          Wert, der verglichen werden soll
     * @param compareValue
     *          Wert, mit dem verglichen werden soll
     * @return
     *          true, wenn der Wert den Vergleich "bestanden" hat
     */
    protected abstract boolean evaluate(R value, R compareValue);

}
