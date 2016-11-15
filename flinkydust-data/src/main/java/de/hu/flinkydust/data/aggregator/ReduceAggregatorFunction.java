package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataSource;

/**
 * Abstrakte Basis-Klasse, die Aggregationen mit einem einfachen Reduce-Schritt durchführt.
 * @param <T>
 *          Der Typ de
 */
public abstract class ReduceAggregatorFunction<T> implements AggregatorFunction<T> {

    @Override
    public DataSource<T> aggregate(DataSource<T> dataSource, int count) {
        DataSource<T> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        return countedDataSource.reduce((tuple1, tuple2) -> reduce(tuple1, tuple2));
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
