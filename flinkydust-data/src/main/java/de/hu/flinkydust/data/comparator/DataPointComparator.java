package de.hu.flinkydust.data.comparator;

import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

/**
 * Abstrakte Basis-Klasse, um Komparator-Funktionen auf einem Feld eines Datensatzes zu implementieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public abstract class DataPointComparator<R extends Comparable<R>> implements Predicate<DustDataPoint> {

    private String field;
    private R compareValue;
    private Class<R> compareClass;

    public DataPointComparator(String field, R compareValue, Class<R> compareClass) {
        this.field = field;
        this.compareValue = compareValue;
        this.compareClass = compareClass;
    }

    @Override
    public boolean test(DustDataPoint t) {
        int index = t.getFieldIndex(field);
        Object tupleValue;
        if (t.getOptionalValue(index).isPresent()) {
            if ((tupleValue = t.getOptionalValue(index).get()).getClass().isAssignableFrom(compareClass)) {
                R value = compareClass.cast(tupleValue);
                return (evaluate(value, compareValue));
            }
            throw new ClassCastException("Das Feld " + field + " von Tupel " + t.toString() + " kann nicht zu Comparable gecasted werden.");
        } else {
            return false;
        }
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

    public static AtLeastComparator<?> dataPointAtLeastComparator(String field, String compareValue) throws IllegalArgumentException {
        if (field.equals("date")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date;
            try {
                date = dateFormat.parse(compareValue);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Date could not be parsed.");
            }
            return new AtLeastComparator<>(field, date, Date.class);
        } else {
            return new AtLeastComparator<>(field, Double.valueOf(compareValue), Double.class);
        }
    }

    public static LessThanComparator<?> dataPointLessThanComparator(String field, String compareValue) throws IllegalArgumentException {
        if (field.equals("date")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date;
            try {
                date = dateFormat.parse(compareValue);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Date could not be parsed.");
            }
            return new LessThanComparator<>(field, date, Date.class);
        } else {
            return new LessThanComparator<>(field, Double.valueOf(compareValue), Double.class);
        }
    }

    public static SameComparator<?> dataPointSameComparator(String field, String compareValue) throws IllegalArgumentException {
        if (field.equals("date")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date;
            try {
                date = dateFormat.parse(compareValue);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Date could not be parsed.");
            }
            return new SameComparator<>(field, date, Date.class);
        } else {
            return new SameComparator<>(field, Double.valueOf(compareValue), Double.class);
        }
    }

}
