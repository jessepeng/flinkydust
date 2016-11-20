package de.hu.flinkydust.data.comparator;

/**
 * Komparator, der Tupel annimmt, bei denen der Wert des gewünschten Feldes mit dem Vergleichswert übereinstimmt.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class SameComparator<R extends Comparable<R>> extends DataPointComparator<R> {

    public SameComparator(String field, R compareValue, R missingValue, Class<R> compareClass) {
        super(field, compareValue, missingValue, compareClass);
    }

    @Override
    protected boolean evaluate(R value, R compareValue) {
        return value.compareTo(compareValue) == 0;
    }
}
