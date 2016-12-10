package de.hu.flinkydust.data.comparator;

/**
 * Komparator, der Datensätze annimmt, bei denen der Wert des gewünschten Feldes mindestens den Vergleichswert aufweist.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class AtLeastComparator<R extends Comparable<R>> extends DataPointComparator<R> {

    public AtLeastComparator(String field, R compareValue, R missingValue, Class<R> compareClass) {
        super(field, compareValue, missingValue, compareClass);
    }

    @Override
    protected boolean evaluate(R value, R compareValue) {
        return value.compareTo(compareValue) > 0;
    }
}
