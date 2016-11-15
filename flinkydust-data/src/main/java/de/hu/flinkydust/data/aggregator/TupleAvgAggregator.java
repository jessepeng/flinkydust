package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.Option;
import scopt.Opt;

import java.util.Optional;

/**
 * Aggregiert mehrere Tupel auf ein Tupel mit dem durchschnittlichen Wert.
 *
 * @param <T>
 *      Klasse der Werte, die aggregiert werden sollen
 * @param <R>
 *     Klasse des Feldes des Tupels, das aggregiert werden soll. Muss das Interface {@link Number} implentieren.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class TupleAvgAggregator<T extends Tuple, R extends Number> implements AggregatorFunction<T> {

    private Class<R> numberClass;

    private int field;

    public TupleAvgAggregator(int field, Class<R> numberClass) {
        this.field = field;
        this.numberClass = numberClass;
    }

    private Tuple2<T, Long> reduce(Tuple2<T, Long> value1, Tuple2<T, Long> value2) {
        Object field1 = value1.f0.getField(field);
        Object field2 = value2.f0.getField(field);
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

        T newTuple = value1.f0;

        boolean oneNaN = false;
        boolean twoNaN = false;

        if (field1Class.isAssignableFrom(Integer.class) && field2Class.isAssignableFrom(Integer.class)) {
            newTuple.setField(number1.intValue() + number2.intValue(), field);
        } else if (field1Class.isAssignableFrom(Long.class) && field2Class.isAssignableFrom(Long.class)) {
            newTuple.setField(number1.longValue() + number2.longValue(), field);
        } else if (field1Class.isAssignableFrom(Short.class) && field2Class.isAssignableFrom(Short.class)) {
            newTuple.setField(number1.shortValue() + number2.shortValue(), field);
        } else if (field1Class.isAssignableFrom(Double.class) && field2Class.isAssignableFrom(Double.class)) {
            Double double1 = number1.doubleValue();
            Double double2 = number2.doubleValue();
            oneNaN = double1.isNaN();
            twoNaN = double2.isNaN();
            newTuple.setField((oneNaN ? 0.0 : double1) + (twoNaN ? 0.0 : double2), field);
        } else if (field1Class.isAssignableFrom(Float.class) && field2Class.isAssignableFrom(Float.class)) {
            Float float1 = number1.floatValue();
            Float float2 = number2.floatValue();
            oneNaN = float1.isNaN();
            twoNaN = float2.isNaN();
            newTuple.setField((oneNaN ? 0.0 : float1) + (twoNaN ? 0.0 : float2), field);
        }
        return new Tuple2<>(newTuple, (oneNaN ? 0L : value1.f1) + (twoNaN ? 0L : value2.f1));
    }

    @Override
    public DataSource<T> aggregate(DataSource<T> dataSource, int count) {
        DataSource<T> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        DataSource<Tuple2<T, Long>> projectedDataSource = countedDataSource.projection(new MapFunction<T, Tuple2<T, Long>>() {
            @Override
            public Tuple2<T, Long> map(T value) throws Exception {
                return new Tuple2<>(value, 1L);
            }
        });
        DataSource<Tuple2<T, Long>> reducedDataSource = projectedDataSource.reduce((tuple1, tuple2) -> reduce(tuple1, tuple2));
        return reducedDataSource.projection(new MapFunction<Tuple2<T, Long>, T>() {
            @Override
            public T map(Tuple2<T, Long> value) throws Exception {
                Object fieldValue = value.f0.getField(field);
                R number;
                Class<?> fieldClass;
                if ((fieldClass = fieldValue.getClass()).isAssignableFrom(numberClass)) {
                    number = numberClass.cast(fieldValue);
                    T tuple = value.f0;

                    if (fieldClass.isAssignableFrom(Integer.class)) {
                        tuple.setField(number.intValue() / value.f1, field);
                    } else if (fieldClass.isAssignableFrom(Long.class)) {
                        tuple.setField(number.longValue() / value.f1, field);
                    } else if (fieldClass.isAssignableFrom(Short.class)) {
                        tuple.setField(number.shortValue() / value.f1, field);
                    } else if (fieldClass.isAssignableFrom(Double.class)) {
                        tuple.setField(number.doubleValue() / value.f1, field);
                    } else if (fieldClass.isAssignableFrom(Float.class)) {
                        tuple.setField(number.floatValue() / value.f1, field);
                    }
                    
                    return tuple;
                } else {
                    throw new ClassCastException("Das Feld " + field + " von Tupel " + value.toString() + " kann nicht zu " + numberClass.getSimpleName() + " gecasted werden.");
                }
            }
        });
    }
}
