package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.util.Date;

/**
 * Aggregiert mehrere DataPoints zu einem Datenpunkt.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class DataPointAggregator {

    public static AggregatorFunction<DustDataPoint> dataPointMinAggregator(String field) {
        if (field.equals("MasterTime")) {
            return new MinAggregator<>(field, Date.class);
        } else {
            return new MinAggregator<>(field, Double.class);
        }
    }

    public static AggregatorFunction<DustDataPoint> dataPointMaxAggregator(String field) {
        if (field.equals("MasterTime")) {
            return new MaxAggregator<>(field, Date.class);
        } else {
            return new MaxAggregator<>(field, Double.class);
        }
    }

    public static AggregatorFunction<DustDataPoint> dataPointAvgAggregator(String field) {
        if (field.equals("MasterTime")) {
            throw new IllegalStateException("Kann keinen Durchschnitt über date bilden.");
        } else {
            return new AvgAggregator(field);
        }
    }

}
