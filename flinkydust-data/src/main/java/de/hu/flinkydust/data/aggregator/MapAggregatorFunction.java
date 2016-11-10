//package de.hu.flinkydust.data.aggregator;
//
//import de.hu.flinkydust.data.DataSource;
//
//import java.io.Serializable;
//
///**
// * Abstrakte Basis-Klasse, die Aggregator-Funktionen ermöglicht. Eine Aggregation besteht aus einem
// * optionalen map-Schritt und einem anschließenden reduce-Schritt.
// *
// * Created by Jan-Christopher on 09.11.2016.
// */
//public abstract class MapAggregatorFunction<T, R> implements Serializable, AggregatorFunction<T> {
//
//    /**
//     * Mappt einen Datensatz auf einen anderen Datensatz,
//     * @param value
//     *          Das Objekt, das gemapped werden soll.
//     * @return
//     *          Das gemappte Objekt.
//     */
//    abstract R map(T value);
//
//    /**
//     * Reduziert zwei Datentypen auf einen Datentyp.
//     * @param value1
//     *          Erster Datensatz
//     * @param value2
//     *          Zweiter Datensatz
//     * @return
//     *          Der reduzierte Datensatz
//     */
//    abstract R reduce(R value1, R value2);
//
//    /**
//     * Mappt den neuen Datentyp wieder zurück auf die Klasse des ursprünglichen Datensatzes.
//     * @param value
//     *          Datensatz
//     * @return
//     *          Der gemappted Datensatz.
//     */
//    abstract T mapBack(R value);
//
//    @Override
//    public DataSource<T> aggregate(DataSource<T> dataSource, int count) {
//        DataSource<T> countedDataSource = (count > 0 ? dataSource.firstN(count) : dataSource);
//        DataSource<R> projectedDataSource = countedDataSource.projection(tuple -> (R)map(tuple));
//        DataSource<R> reducedDataSource = projectedDataSource.reduce((tuple1, tuple2) -> (R)reduce(tuple1, tuple2));
//        return reducedDataSource.projection(tuple -> (T)mapBack(tuple));
//    }
//}
