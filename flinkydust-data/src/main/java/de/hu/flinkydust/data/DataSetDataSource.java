package de.hu.flinkydust.data;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple5;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * DataSource, die ein Flink {@link DataSet} als Speicherstruktur verwendet.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class DataSetDataSource<T> implements DataSource<T> {

    private DataSet<T> wrappedDataSet;

    /**
     * Erzeugt eine neue DataSetDataSource aus einem vorhandenen Flink {@link DataSet}.
     *
     * @param dataSource
     *          Das Flink {@link DataSet}, das als Speicherstruktur verwendet werden soll.
     */
    public DataSetDataSource(DataSet<T> dataSource) {
        this.wrappedDataSet = dataSource;
    }

    /**
     * Liest eine CSV-Datei ein, speichert die Datensätze gegebenenfalls in einer Collection in-memory und gibt eine DataSource mit einem 5-elementigen Tupel als Datentyp zurück.
     * @param environment
     *          Die Flink-Ausführungsumgebung
     * @param path
     *          Der Pfad zur Datei
     * @param inMemory
     *          Gibt an, ob die Daten in-Memory vorgehalten werden sollen
     * @param type0
     *          Der Typ des ersten Felds der CSV-Datei
     * @param type1
     *          Der Typ des zweiten Felds der CSV-Datei
     * @param type2
     *          Der Typ des dritten Felds der CSV-Datei
     * @param type3
     *          Der Typ des vierten Felds der CSV-Datei
     * @param type4
     *          Der Typ des fünften Felds der CSV-Datei
     * @param <T0>
     *          Der Typ des ersten Felds der CSV-Datei
     * @param <T1>
     *          Der Typ des zweiten Felds der CSV-Datei
     * @param <T2>
     *          Der Typ des dritten Felds der CSV-Datei
     * @param <T3>
     *          Der Typ des vierten Felds der CSV-Datei
     * @param <T4>
     *          Der Typ des fünften Felds der CSV-Datei
     * @return
     *          Die DataSource mit den Datensätzen aus der CSV-Datei
     */
    public static <T0, T1, T2, T3, T4> DataSource<Tuple5<T0, T1, T2, T3, T4>> readFile(ExecutionEnvironment environment, String path, boolean inMemory, Class<T0> type0, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        DataSource<Tuple5<T0, T1, T2, T3, T4>> dataSource = new DataSetDataSource<>(environment.readCsvFile(path).fieldDelimiter(";").ignoreFirstLine().types(type0, type1, type2, type3, type4));
        if (inMemory) {
            return createInMemoryDataSource(environment, dataSource);
        }
        return dataSource;
    }

    /**
     * Liest eine CSV-Datei mit den Staubdaten ein und erzeugt eine neue DataSetDataSource mit einer In-Memory-Collection der Daten.
     * @param environment
     *          Die Flink Ausführungsumgebung
     * @param path
     *          Der Pfad zur CSV-Datei mit den Staubdaten
     * @return
     *          Die DataSource mit den Datensätzen aus der CSV-Datei
     */
    public static DataSource<Tuple5<Date, Double, Double, Double, Double>> readFile(ExecutionEnvironment environment, String path) {
        DataSource<Tuple5<String, String, String, String, String>> csvDataSource = readFile(
                environment,
                path,
                false,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class
        );
        DataSource<Tuple5<Date, Double, Double, Double, Double>> dateDataSource = csvDataSource.projection(new TupleParseProjection());
        return createInMemoryDataSource(environment, dateDataSource);
    }


    /**
     * Projection-Funktion, die den eingelesenen Tupeln die korrekten Datentypen gibt und die Werte parst.
     */
    private static class TupleParseProjection implements MapFunction<Tuple5<String, String, String, String, String>, Tuple5<Date, Double, Double, Double, Double>> {
        public Tuple5<Date, Double, Double, Double, Double> map(Tuple5<String, String, String, String, String> tuple) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date();
            try {
                date = dateFormat.parse(tuple.f0);
            } catch (ParseException e) {
                // Ignore
            }
            return new Tuple5<Date, Double, Double, Double, Double>(date,
                    (tuple.f1.equals("NA") ? Double.NaN : Double.valueOf(tuple.f1)),
                    (tuple.f2.equals("NA") ? Double.NaN : Double.valueOf(tuple.f2)),
                    (tuple.f3.equals("NA") ? Double.NaN : Double.valueOf(tuple.f3)),
                    (tuple.f4.equals("NA") ? Double.NaN : Double.valueOf(tuple.f4)));
        }
    }

    /**
     * Erzeugt eine In-Memory-Kopie der angegebenen DataSource, um die Daten schneller zur Verfügung zu haben.
     * @param environment
     *          Die Flink Ausführungsumgebung
     * @param dataSource
     *          Die DataSource, von der eine In-Memory-Kopie erzeugt werden soll
     * @param <T0>
     *          Der Typ des ersten Felds des Tupels
     * @param <T1>
     *          Der Typ des zweiten Felds des Tupels
     * @param <T2>
     *          Der Typ des dritten Felds des Tupels
     * @param <T3>
     *          Der Typ des vierten Felds des Tupels
     * @param <T4>
     *          Der Typ des fünften Felds des Tupels
     * @return
     *          DataSource mit In-Memory-Kopie der ursprünglichen DataSource
     */
    private static <T0, T1, T2, T3, T4> DataSource<Tuple5<T0, T1, T2, T3, T4>> createInMemoryDataSource(ExecutionEnvironment environment, DataSource<Tuple5<T0, T1, T2, T3, T4>> dataSource) {
        try {
            List<Tuple5<T0, T1, T2, T3, T4>> dataList = dataSource.collect();
            return new DataSetDataSource<>(environment.fromCollection(dataList));
        } catch (Exception e) {
            throw new RuntimeException("Konnte die Datei nicht einlesen. Grund: " + e.getMessage(), e);
        }
    }

    @Override
    public DataSource<T> selection(FilterFunction<T> predicate) {
        return new DataSetDataSource<T>(wrappedDataSet.filter(predicate));
    }

    @Override
    public <R> DataSource<R> projection(MapFunction<T, R> projector) {
        return new DataSetDataSource<R>(wrappedDataSet.map(projector));
    }

    @Override
    public DataSource<T> reduce(ReduceFunction<T> reducer) {
        return new DataSetDataSource<T>(wrappedDataSet.reduce(reducer));
    }

    @Override
    public List<T> collect() throws Exception {
        return wrappedDataSet.collect();
    }

    @Override
    public DataSource<T> firstN(int count) {
        return new DataSetDataSource<>(wrappedDataSet.first(count));
    }

    @Override
    public void print(){
        /* Vorsicht: kann viele Daten enthalten */
        try {
            this.wrappedDataSet.print();
        } catch (Exception e) {
            throw new RuntimeException("Konnte Daten nicht ausgeben. Grund: " + e.getMessage(), e);
        }
    }
}
