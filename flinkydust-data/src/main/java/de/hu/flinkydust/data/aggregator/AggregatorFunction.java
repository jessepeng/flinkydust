package de.hu.flinkydust.data.aggregator;

import de.hu.flinkydust.data.DataSource;

import java.io.Serializable;

/**
 * Interfaces, das Aggregator-Funktionen ermöglicht.
 *
 * @param <T>
 *          Datentyp, der aggregiert werden soll.
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
    DataSource<T> aggregate(DataSource<T> dataSource, int count);

}
