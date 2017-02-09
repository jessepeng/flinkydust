package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.SimpleTuple;
import de.hu.flinkydust.data.tuple.NoFieldMappingException;

import java.util.Optional;

/**
 * Aggregiert mehrere Tupel auf ein Tupel mit dem durchschnittlichen Wert.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class AvgAggregator implements AggregatorFunction<DustDataPoint> {

    /**
     * Das Feld, über das aggregiert werden soll.
     */
    private String field;
    /**
     * Index des Feldes
     */
    private int index;

    public AvgAggregator(String field) {
        if(field.equals("MasterTime")){
            throw new ClassCastException("Kann keinen Durschnitt über 'date' bilden.");
        }

        this.field = field;
    }

    /**
     * Aggregiert zwei DustDataPoints.
     * @param value1
     *      Der erste DustDatapoint, der aggregiert werden soll
     * @param value2
     *      Der zweite DustDatapoint, der aggregiert werden soll
     * @return
     */
    private SimpleTuple<DustDataPoint, Long> reduce(SimpleTuple<DustDataPoint, Long> value1, SimpleTuple<DustDataPoint, Long> value2) {
        try {
            index = value1.f0.getFieldIndex(field);
        } catch (NoFieldMappingException e) {
            index = value2.f0.getFieldIndex(field);
        }

        Optional<Double> field1 = value1.f0.getOptionalValue(index);
        Optional<Double> field2 = value2.f0.getOptionalValue(index);

        DustDataPoint newTuple = value2.f0;
        Double f1 = field1.orElse(0.0);
        Double f2 = field2.orElse(0.0);

        Optional<Double> v = Optional.of(f1 + f2);
        newTuple.setField(v, index);

        return new SimpleTuple<>(newTuple, (field1.isPresent() ? value1.f1 : 0L ) + (field2.isPresent() ? value2.f1 : 0L));
    }

    /**
     * Aggregiert n DustDataPoints einer DataSource.
     * @param dataSource
     *          Die DataSource mit den Datensätzen.
     * @param count
     *          Die Anzahl an Datensätzen, die aggregiert werden sollen. Wenn count &gt; 0, dann alle Datensätze.
     * @return
     */
    @Override
    public DataSource<DustDataPoint> aggregate(DataSource<DustDataPoint> dataSource, int count) {
        DataSource<DustDataPoint> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        DataSource<SimpleTuple<DustDataPoint, Long>> projectedDataSource = countedDataSource.projection(dataPoint -> new SimpleTuple<>(dataPoint, 1L));
        DataSource<SimpleTuple<DustDataPoint, Long>> reducedDataSource = projectedDataSource.reduce(new SimpleTuple<>(new DustDataPoint(), 0L), this::reduce);
        return reducedDataSource.projection(value -> {
            Optional<Double> fieldValue = value.f0.getOptionalValue(index);

            DustDataPoint tuple = value.f0;
            Double number = fieldValue.orElse(0.0);
            tuple.setField(field, number / value.f1);
            return tuple;
        });
    }

}