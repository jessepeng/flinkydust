package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataSource;

import java.io.Serializable;

/**
 * Abstrakte Basis-Klasse, die Aggregator-Funktionen ermöglicht.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public interface AggregatorFunction<T> extends Serializable {

    /**
     * Aggregiert alle oder die angegebene Anzahl an Datensätzen der angegebenen DataSource auf eine neue DataSource.
     * @param dataSource
     *          Die DataSource mit den Datensätzen.
     * @param count
     *          Die Anzahl an Datensätzen, die aggregiert werden sollen. Wenn count &lt; 0, dann alle Datensätze.
     * @return
     *          Die neue DataSource mit dem aggregierten Datensatz.
     */
    default DataSource<T> aggregate(DataSource<T> dataSource, int count) {
        DataSource<T> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        return countedDataSource.reduce((tuple1, tuple2) -> reduce(tuple1, tuple2));
    }

    T reduce(T value1, T value2);

}
