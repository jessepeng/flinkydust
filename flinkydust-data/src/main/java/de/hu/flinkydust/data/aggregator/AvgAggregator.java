package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import java.util.Optional;

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
    private int index;

    public AvgAggregator(String field, Class<R> numberClass) {
        if(field.equals("date")){
            throw new ClassCastException("Kann keinen Durschnitt über 'date' bilden.");
        }

        this.field = field;
        this.numberClass = numberClass;
    }

    private Tuple<DataPoint, Long> reduce(Tuple<DataPoint, Long> value1, Tuple<DataPoint, Long> value2) {
        this.index = value1.f0.getFieldIndex(field);

        Optional<Double> field1 = value1.f0.getOptionalValue(index);
        Optional<Double> field2 = value2.f0.getOptionalValue(index);


        DataPoint newTuple = value1.f0;
        Double f1 = field1.orElse(0.0);
        Double f2 = field2.orElse(0.0);

        Optional<Double> v = Optional.of(f1+f2);
        newTuple.setField(v, index);

        return new Tuple<>(newTuple, (field1.isPresent() ? value1.f1 : 0L ) + (field2.isPresent() ? value2.f1 : 0L));
    }

    @Override
    public DataSource<DataPoint> aggregate(DataSource<DataPoint> dataSource, int count) {
        DataSource<DataPoint> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        DataSource<Tuple<DataPoint, Long>> projectedDataSource = countedDataSource.projection(dataPoint -> new Tuple<>(dataPoint, 1L));
        DataSource<Tuple<DataPoint, Long>> reducedDataSource = projectedDataSource.reduce(new Tuple<>(new DataPoint(), 0L), this::reduce);
        return reducedDataSource.projection(value -> {
            Optional<Double> fieldValue = value.f0.getOptionalValue(index);

            if (fieldValue.isPresent()) {
                DataPoint tuple = value.f0;
                Double number = fieldValue.get();
                tuple.setField(number / value.f1, index);
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