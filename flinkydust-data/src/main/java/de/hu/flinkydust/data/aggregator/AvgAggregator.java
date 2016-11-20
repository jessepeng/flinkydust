package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;

/**
 * Aggregiert mehrere Tupel auf ein Tupel mit dem durchschnittlichen Wert.
 *
 * @param <R>
 *     Klasse des Feldes des Tupels, das aggregiert werden soll. Muss das Interface {@link Number} implentieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class AvgAggregator<R extends Number> implements AggregatorFunction<DataPoint> {

    private Class<R> numberClass;

    private String field;

    public AvgAggregator(String field, Class<R> numberClass) {
        this.field = field;
        this.numberClass = numberClass;
    }

    private Tuple<DataPoint, Long> reduce(Tuple<DataPoint, Long> value1, Tuple<DataPoint, Long> value2) {
        Object field1 = value1.f0.getField(Integer.valueOf(field));
        Object field2 = value2.f0.getFieldIndex(field);
        R number1, number2;
        Class<?> field1Class, field2Class;
        if ((field1Class = field1.getClass()).isAssignableFrom(numberClass)) {
            number1 = numberClass.cast(field1);
            if ((field2Class = field2.getClass()).isAssignableFrom(numberClass)) {
                number2 = numberClass.cast(field2);
            } else {
                throw new ClassCastException("Das Feld " + field + " von Tupel " + value2.toString() + " kann nicht zu " + numberClass.getSimpleName() + " gecasted werden.");
            }
        } else {
            throw new ClassCastException("Das Feld " + field + " von Tupel " + value1.toString() + " kann nicht zu " + numberClass.getSimpleName() + " gecasted werden.");
        }

        DataPoint newTuple = value1.f0;

        boolean oneNaN = false;
        boolean twoNaN = false;

        if (field1Class.isAssignableFrom(Integer.class) && field2Class.isAssignableFrom(Integer.class)) {
            newTuple.setField(number1.intValue() + number2.intValue(), Integer.valueOf(field));
        } else if (field1Class.isAssignableFrom(Long.class) && field2Class.isAssignableFrom(Long.class)) {
            newTuple.setField(number1.longValue() + number2.longValue(), Integer.valueOf(field));
        } else if (field1Class.isAssignableFrom(Short.class) && field2Class.isAssignableFrom(Short.class)) {
            newTuple.setField(number1.shortValue() + number2.shortValue(), Integer.valueOf(field));
        } else if (field1Class.isAssignableFrom(Double.class) && field2Class.isAssignableFrom(Double.class)) {
            Double double1 = number1.doubleValue();
            Double double2 = number2.doubleValue();
            oneNaN = double1.isNaN();
            twoNaN = double2.isNaN();
            newTuple.setField((oneNaN ? 0.0 : double1) + (twoNaN ? 0.0 : double2), Integer.valueOf(field));
        } else if (field1Class.isAssignableFrom(Float.class) && field2Class.isAssignableFrom(Float.class)) {
            Float float1 = number1.floatValue();
            Float float2 = number2.floatValue();
            oneNaN = float1.isNaN();
            twoNaN = float2.isNaN();
            newTuple.setField((oneNaN ? 0.0 : float1) + (twoNaN ? 0.0 : float2), Integer.valueOf(field));
        }
        return new Tuple<>(newTuple, (oneNaN ? 0L : value1.f1) + (twoNaN ? 0L : value2.f1));
    }

    @Override
    public DataSource<DataPoint> aggregate(DataSource<DataPoint> dataSource, int count) {
        DataSource<DataPoint> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        DataSource<Tuple<DataPoint, Long>> projectedDataSource = countedDataSource.projection(dataPoint -> new Tuple<>(dataPoint, 1L));
        DataSource<Tuple<DataPoint, Long>> reducedDataSource = projectedDataSource.reduce(new Tuple<>(new DataPoint(), 0L), this::reduce);
        return reducedDataSource.projection(value -> {
            Object fieldValue = value.f0.getField(Integer.valueOf(field));
            R number;
            Class<?> fieldClass;
            if ((fieldClass = fieldValue.getClass()).isAssignableFrom(numberClass)) {
                number = numberClass.cast(fieldValue);
                DataPoint tuple = value.f0;

                if (fieldClass.isAssignableFrom(Integer.class)) {
                    tuple.setField(number.intValue() / value.f1, Integer.valueOf(field));
                } else if (fieldClass.isAssignableFrom(Long.class)) {
                    tuple.setField(number.longValue() / value.f1, Integer.valueOf(field));
                } else if (fieldClass.isAssignableFrom(Short.class)) {
                    tuple.setField(number.shortValue() / value.f1, Integer.valueOf(field));
                } else if (fieldClass.isAssignableFrom(Double.class)) {
                    tuple.setField(number.doubleValue() / value.f1, Integer.valueOf(field));
                } else if (fieldClass.isAssignableFrom(Float.class)) {
                    tuple.setField(number.floatValue() / value.f1, Integer.valueOf(field));
                }

                return tuple;
            } else {
                throw new ClassCastException("Das Feld " + field + " von Tupel " + value.toString() + " kann nicht zu " + numberClass.getSimpleName() + " gecasted werden.");
            }
        });
    }

    public class Tuple<DataPoint, Y> {
        public final DataPoint f0;
        public final Y f1;
        public Tuple(DataPoint f0, Y f1) {
            this.f0 = f0;
            this.f1 = f1;
        }


    }
}