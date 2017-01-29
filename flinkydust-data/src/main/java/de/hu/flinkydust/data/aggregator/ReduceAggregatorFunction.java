package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

/**
 * Abstrakte Basis-Klasse, die Aggregationen mit einem einfachen Reduce-Schritt durchführt.
 * @param <T>
 *          Der Typ de
 */
public abstract class ReduceAggregatorFunction<T extends EuclidianDistanceDataPoint> implements AggregatorFunction<T> {

    private T identity;

    public ReduceAggregatorFunction(T identity) {
        this.identity = identity;
    }

    @Override
    public DataSource<T> aggregate(DataSource<T> dataSource, int count) {
        DataSource<T> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        return countedDataSource.reduce(identity, this::reduce);
    }

    /**
     * Der eigetliche Aggreagtionsschritt, der durchgeführt werden soll.
     * @param value1
     *          Linker Wert
     * @param value2
     *          Rechter Wert
     * @return
     *          Der aggregierte Wert
     */
    protected abstract T reduce(T value1, T value2);

}
