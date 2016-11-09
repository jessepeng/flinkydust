package de.hu.flinkydust.data.comparator;

import org.apache.flink.api.java.tuple.Tuple;

import java.util.function.Predicate;

/**
 * Abstarkte Basis-Klasse, um Komparator-Funktionen auf einem Feld eines Tupels zu implementieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public abstract class TupleFieldComparator<T extends Tuple, R extends Comparable<R>> implements Predicate<T> {

    private int field;
    private R compareValue;
    private Class<R> compareClass;

    public TupleFieldComparator(int field, R compareValue, Class<R> compareClass) {
        this.field = field;
        this.compareValue = compareValue;
        this.compareClass = compareClass;
    }

    @Override
    public boolean test(T t) {
        Object tupleValue;
        if ((tupleValue = t.getField(field)).getClass().isAssignableFrom(compareClass)) {
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
