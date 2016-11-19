package de.hu.flinkydust.data;

import org.apache.flink.api.java.DataSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DataSource, die ein Flink {@link DataSet} als Speicherstruktur verwendet.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class StreamDataSource<T> implements DataSource<T> {

    private Stream<T> wrappedStream;

    /**
     * Erzeugt eine neue StreamDataSource aus einem vorhandenen Flink {@link DataSet}.
     *
     * @param dataSource
     *          Das Flink {@link DataSet}, das als Speicherstruktur verwendet werden soll.
     */
    public StreamDataSource(Stream<T> dataSource) {
        this.wrappedStream = dataSource;
    }

    /**
     * Liest eine CSV-Datei mit den Staubdaten ein und erzeugt eine neue StreamDataSource mit einer In-Memory-Collection der Daten.
     * @param path
     *          Der Pfad zur CSV-Datei mit den Staubdaten
     * @return
     *          Die DataSource mit den Datensätzen aus der CSV-Datei
     */
    public static DataSource<DataPoint> readFile(String path) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(path))) {
            String line;
            // Erste Zeile überspringen
            fileReader.readLine();
            List<DataPoint> dataPoints = new LinkedList<>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            while ((line = fileReader.readLine()) != null) {
                String[] fields = line.split(";");
                Date date = null;
                try {
                   date = dateFormat.parse(fields[0]);
                } catch (ParseException e) {
                }
                dataPoints.add(new DataPoint(
                        date,
                        fields[1].equals("NA") ? null : Double.valueOf(fields[1]),
                        fields[2].equals("NA") ? null : Double.valueOf(fields[2]),
                        fields[3].equals("NA") ? null : Double.valueOf(fields[3]),
                        fields[4].equals("NA") ? null : Double.valueOf(fields[4])
                ));
            }

            return new StreamDataSource<>(dataPoints.stream());
        } catch (IOException e) {
            System.err.println("Konnte datei nicht einlesen: " + e.getMessage());
            return new StreamDataSource<>(Stream.empty());
        }
    }

    @Override
    public DataSource<T> selection(Predicate<? super T> predicate) {
        return new StreamDataSource<>(wrappedStream.filter(predicate));
    }

    @Override
    public <R> DataSource<R> projection(Function<? super T, ? extends R> projector) {
        return new StreamDataSource<>(wrappedStream.map(projector));
    }

    @Override
    public DataSource<T> reduce(T identity, BinaryOperator<T> reducer) {
        return new StreamDataSource<>(Stream.of(wrappedStream.reduce(identity, reducer)));
    }

    @Override
    public List<T> collect() throws Exception {
        return wrappedStream.collect(Collectors.toList());
    }

    @Override
    public DataSource<T> firstN(int count) {
        return new StreamDataSource<>(wrappedStream.limit(count));
    }

    @Override
    public void print(){
        /* Vorsicht: kann viele Daten enthalten */
        try {
            this.wrappedStream.collect(Collectors.toList()).forEach(System.out::print);
        } catch (Exception e) {
            throw new RuntimeException("Konnte Daten nicht ausgeben. Grund: " + e.getMessage(), e);
        }
    }
}
