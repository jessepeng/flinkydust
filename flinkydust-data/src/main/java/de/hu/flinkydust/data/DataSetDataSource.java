package de.hu.flinkydust.data;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple5;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * DataSource, die ein Flink {@link DataSet} als Speicherstruktur verwendet.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class DataSetDataSource<T> implements DataSource<T> {

    private DataSet<T> wrappedDataSet;

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
        DataSource<Tuple5<T0, T1, T2, T3, T4>> dataSource = new DataSetDataSource<>(environment.readCsvFile(path).types(type0, type1, type2, type3, type4));
        if (inMemory) {
            return createInMemoryDataSource(environment, dataSource);
        }
        return dataSource;
    }

    public static DataSource<Tuple5<Date, Integer, Integer, Float, Float>> readFile(ExecutionEnvironment environment, String path) {
        DataSource<Tuple5<String, Integer, Integer, Float, Float>> csvDataSource = readFile(
                environment,
                path,
                false,
                String.class,
                Integer.class,
                Integer.class,
                Float.class,
                Float.class
        );
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> dateDataSource = csvDataSource.projection(tuple -> {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date();
            try {
                date = dateFormat.parse(tuple.f0);
            } catch (ParseException e) {
                // Ignore
            }
            return new Tuple5<>(date, tuple.f1, tuple.f2, tuple.f3, tuple.f4);
        });
        return createInMemoryDataSource(environment, dateDataSource);
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
    public DataSource<T> selection(Predicate<T> predicate) {
        return new DataSetDataSource<>(wrappedDataSet.filter(predicate::test));
    }

    @Override
    public <R> DataSource<R> projection(Function<T, R> projector) {
        return new DataSetDataSource<>(wrappedDataSet.map(projector::apply));
    }

    @Override
    public DataSource<T> reduce(BiFunction<T, T, T> reducer) {
        return new DataSetDataSource<>(wrappedDataSet.reduce(reducer::apply));
    }

    @Override
    public List<T> collect() throws Exception {
        return wrappedDataSet.collect();
    }

    @Override
    public DataSource<T> firstN(int count) {
        return new DataSetDataSource<>(wrappedDataSet.first(count));
    }
}
