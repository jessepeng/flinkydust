package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataPoint;

import java.util.Date;

/**
 * Created by Jan-Christopher on 11.12.2016.
 */
public class DataPointAggregator {

    public static AggregatorFunction<DataPoint> dataPointMinAggregator(String field) {
        if (field.equals("date")) {
            return new MinAggregator<>(field, Date.class);
        } else {
            return new MinAggregator<>(field, Double.class);
        }
    }

    public static AggregatorFunction<DataPoint> dataPointMaxAggregator(String field) {
        if (field.equals("date")) {
            return new MaxAggregator<>(field, Date.class);
        } else {
            return new MaxAggregator<>(field, Double.class);
        }
    }

    public static AggregatorFunction<DataPoint> dataPointAvgAggregator(String field) {
        if (field.equals("date")) {
            throw new IllegalStateException("Kann keinen Durchschnitt über date bilden.");
        } else {
            return new AvgAggregator(field);
        }
    }

}
