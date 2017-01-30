package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.Tuple;

import java.util.Optional;

/**
 * Aggregiert mehrere Tupel auf ein Tupel mit dem durchschnittlichen Wert.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class AvgAggregator implements AggregatorFunction<DataPoint> {

    private String field;
    private int index;

    public AvgAggregator(String field) {
        if(field.equals("date")){
            throw new ClassCastException("Kann keinen Durschnitt über 'date' bilden.");
        }

        this.field = field;
    }

    private Tuple<DataPoint, Long> reduce(Tuple<DataPoint, Long> value1, Tuple<DataPoint, Long> value2) {
        this.index = value1.f0.getFieldIndex(field);

        Optional<Double> field1 = value1.f0.getOptionalValue(index);
        Optional<Double> field2 = value2.f0.getOptionalValue(index);


        DataPoint newTuple = value1.f0;
        Double f1 = field1.orElse(0.0);
        Double f2 = field2.orElse(0.0);

        Optional<Double> v = Optional.of(f1 + f2);
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

            DataPoint tuple = value.f0;
            Double number = fieldValue.orElse(0.0);
            tuple.setField(field, number / value.f1);
            return tuple;
        });
    }

}