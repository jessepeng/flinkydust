package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.StreamDataSource;
import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregator, der die Daten über ein beliebig wählbares Zeitfenster aggregiert und in einer neuen
 * DataSource ausgibt.
 *
 * Created by Jan-Christopher on 02.02.2017.
 */
public class TimeWindowAggregator implements AggregatorFunction<DustDataPoint> {

    private int hours;

    public TimeWindowAggregator(int hours) {
        if (24 % hours != 0) {
            throw new IllegalArgumentException("Nur Teiler von 24 Stunden können als Zeitfenster gewählt werden.");
        }
        this.hours = hours;
    }

    @Override
    public DataSource<DustDataPoint> aggregate(DataSource<DustDataPoint> dataSource, int count) {
        Map<Date, DustDataPoint> groupedDataPoints = dataSource.stream()
                .map(dataPoint -> {
                    Calendar dataPointCal = new GregorianCalendar();
                    dataPointCal.setTime(dataPoint.getDate());
                    dataPointCal.set(Calendar.MINUTE, 0);
                    dataPointCal.set(Calendar.SECOND, 0);
                    dataPointCal.set(Calendar.MILLISECOND, 0);
                    dataPointCal.set(Calendar.HOUR_OF_DAY, dataPointCal.get(Calendar.HOUR_OF_DAY) / hours);
                    dataPoint.setField("MasterTime", dataPointCal.getTime());
                    return dataPoint;
                })
                .collect(Collectors.groupingBy(DustDataPoint::getDate, HashMap::new, Collectors.reducing(new DustDataPoint(), (dustDataPoint, dustDataPoint2) -> {
                    if (dustDataPoint.getArity() != 0) {
                        for (int i = 0; i < dustDataPoint.getArity(); i++) {
                            if (i != dustDataPoint.getFieldIndex("MasterTime")) {
                                dustDataPoint2.setField(i, dustDataPoint2.getDoubleField(i) + dustDataPoint.getDoubleField(i));
                            }
                        }
                    }
                    return dustDataPoint2;
                })));
        return new StreamDataSource<>(groupedDataPoints.values().stream());
    }
}
