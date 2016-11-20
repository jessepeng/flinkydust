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
import java.util.concurrent.ThreadLocalRandom;
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

    public static DataSource<DataPoint> generateRandomData(Integer size) {
        List<DataPoint> dataPoints = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            dataPoints.add(generateRandomTuple());
        }

        return new StreamDataSource<>(dataPoints.stream());
    }

    private static DataPoint generateRandomTuple() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        DataPoint dataPoint = new DataPoint();
        //generate new dates
        String randomMonth = String.valueOf(ThreadLocalRandom.current().nextDouble(1, 12 + 1));
        String randomDay = String.valueOf(ThreadLocalRandom.current().nextDouble(1, 31 + 1));
        String randomHour = String.valueOf(ThreadLocalRandom.current().nextDouble(0, 23 + 1));
        String randomMinute = String.valueOf(ThreadLocalRandom.current().nextDouble(0, 59 + 1));

        //Add prefix zeroes
        randomMonth = addLeadingZeroes(randomMonth);
        randomDay = addLeadingZeroes(randomDay);
        randomHour = addLeadingZeroes(randomHour);
        randomMinute = addLeadingZeroes(randomMinute);

        String dateString = "2014-" + randomMonth + "-" + randomDay + " " + randomHour + ":" + randomMinute + ":00";
        Date date = new Date();

        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // Ignore
        }

        //generate data for small particles
        Double smallParticles = ThreadLocalRandom.current().nextDouble(0.0, 100000.0 + 1);
        //generate data for large particles
        Double largeParticles = ThreadLocalRandom.current().nextDouble(0.0, 50000.0 + 1);
        //generate data for temperature
        Double temperature = ThreadLocalRandom.current().nextDouble(-50.0, 60.0 + 1);
        //generate data for humidity
        Double humidity = ThreadLocalRandom.current().nextDouble(0.0, 100.0 + 1);

        /*
        double randomError = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        if (randomError > 0.00 && randomError < 0.01) {
            smallParticles = Double.NaN;
        } else if (randomError > 0.01 && randomError < 0.02) {
            largeParticels = Double.NaN;
        } else if (randomError > 0.02 && randomError < 0.03) {
            temperature = Double.NaN;
        } else if (randomError > 0.03 && randomError < 0.04) {
            humidity = Double.NaN;
        }*/

        return new DataPoint(date, smallParticles, largeParticles, humidity, temperature);
    }

    private static String addLeadingZeroes(String string) {
        if (string.length() == 1)
            return "0" + string;
        return string;
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
