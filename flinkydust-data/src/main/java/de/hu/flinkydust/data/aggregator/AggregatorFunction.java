package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataSource;

/**
 * Abstrakte Basis-Klasse, die Aggregator-Funktionen ermöglicht. Eine Aggregation besteht aus einem
 * optionalen map-Schritt und einem anschließenden reduce-Schritt.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public abstract class AggregatorFunction<T, R> {

    /**
     * Mappt einen Datensatz auf einen anderen Datensatz,
     * @param value
     *          Das Objekt, das gemapped werden soll.
     * @return
     *          Das gemappte Objekt.
     */
    protected abstract R map(T value);

    /**
     * Reduziert zwei Datentypen auf einen Datentyp.
     * @param value1
     *          Erster Datensatz
     * @param value2
     *          Zweiter Datensatz
     * @return
     *          Der reduzierte Datensatz
     */
    protected abstract R reduce(R value1, R value2);

    /**
     * Mappt den neuen Datentyp wieder zurück auf die Klasse des ursprünglichen Datensatzes.
     * @param value
     *          Datensatz
     * @return
     *          Der gemappted Datensatz.
     */
    protected abstract T mapBack(R value);

    /**
     * Aggregiert alle oder die angegebene Anzahl an Datensätzen der angegebenen DataSource auf eine neue DataSource.
     * @param dataSource
     *          Die DataSource mit den Datensätzen.
     * @param count
     *          Die Anzahl an Datensätzen, die aggregiert werden sollen. Wenn count < 0, dann alle Datensätze.
     * @return
     *          Die neue DataSource mit dem aggregierten Datensatz.
     */
    public DataSource<T> aggregate(DataSource<T> dataSource, int count) {
        DataSource<T> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
        return countedDataSource.firstN(count).projection(this::map).reduce(this::reduce).projection(this::mapBack);
    }

}
