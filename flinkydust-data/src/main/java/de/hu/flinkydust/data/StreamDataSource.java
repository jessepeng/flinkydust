package de.hu.flinkydust.data;



import de.hu.flinkydust.data.aggregator.AggregatorFunction;
import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.io.*;
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
 * DataSource, die einen Java8 Stream als Speicherstruktur verwendet. Die Methoden {@link DataSource#projection(Function)},
 * {@link DataSource#selection(Predicate)} und {@link DataSource#aggregation(AggregatorFunction)}
 * sind dabei intermediäre Operationen, das heißt sie arbeiten nicht unmittelbar sofort auf den Daten.
 * Erst ein Aufruf der Methode {@link DataSource#collect()} oder {@link DataSource#print()}
 * führt dazu, dass die eigentlichen Daten bearbeitet werden.<br><br>
 *
 * Dies erlaubt das Aneinanderketten verschiedener Operatoren, wie bspw. <code>DataSource.projection(...).selection(...).aggregation(...)</code>.<br><br>
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public class StreamDataSource<T> implements DataSource<T> {

    private Stream<T> wrappedStream;

    /**
     * Erzeugt eine neue StreamDataSource aus einer Liste
     * @param list
     *          Die Liste, aus der die DataSource erzeugt werden soll.
     */
    public StreamDataSource(List<T> list) {
        this.wrappedStream = list.stream();
    }

    /**
     * Erzeugt eine neue StreamDataSource aus einem vorhandenen Java8 Stream.
     *
     * @param dataSource
     *           Der Java8 Stream, das als Speicherstruktur verwendet werden soll.
     */
    public StreamDataSource(Stream<T> dataSource) {
        this.wrappedStream = dataSource;
    }

    /**
     * Liest eine CSV-Datei mit den Staubdaten ein und erzeugt eine neue StreamDataSource mit einer In-Memory-Collection der Daten.
     * @param inputStream
     *          Ein InputStream, der die Daten zur Verfügung stellt
     * @return
     *          Die DataSource mit den Datensätzen aus der CSV-Datei
     * @throws IOException
     *          Wenn eine Ausnahme beim Lesen der Datei auftrat.
     */
    public static DataSource<DustDataPoint> readFile(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return new StreamDataSource<>(readFromReader(reader));
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Liest eine CSV-Datei mit den Staubdaten ein und erzeugt eine neue StreamDataSource mit einer In-Memory-Collection der Daten.
     * @param path
     *          Der Pfad zur CSV-Datei mit den Staubdaten
     * @return
     *          Die DataSource mit den Datensätzen aus der CSV-Datei
     * @throws IOException
     *          Wenn eine Ausnahme beim Lesen der Datei auftrat.
     */
    public static DataSource<DustDataPoint> readFile(String path) throws IOException {
        return new StreamDataSource<>(parseFile(path));
    }

    /**
     * Lese eine Datei mit Staubdaten ein und gib sie als Liste zurück.
     * @param path
     *          Pfad zur Datei
     * @return
     *          Liste mit Datenpunkten
     * @throws IOException
     *          Exception bei Lesefehlern
     */
    public static List<DustDataPoint> parseFile(String path) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(path))) {
            return readFromReader(fileReader);
        } catch (IOException e) {
            System.err.println("Konnte datei nicht einlesen: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Parst eine Datei aus einem InputStream und gibt eine Liste an DataPoints zurück
     * @param inputStream
     *          Der InputStream, von dem gelesen werden soll
     * @return
     *          Die Liste der DataPoints
     * @throws IOException
     *          Wenn ein Fehler beim Lesen aufgetreten ist
     */
    public static List<DustDataPoint> parseFile(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readFromReader(reader);
        } catch (IOException e) {
            throw e;
        }
    }

    private static List<DustDataPoint> readFromReader(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String[] headerNames = line.split(";");
        List<DustDataPoint> dataPoints = new LinkedList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(";");
            Object[] fieldsConverted = new Object[fields.length];
            try {
                fieldsConverted[0] = dateFormat.parse(fields[0]);
            } catch (ParseException e) {
            }
            for (int i = 1; i < fields.length; i++) {
                fieldsConverted[i] = fields[i].equals("NA") ? null : Double.valueOf(fields[i]);
            }
            dataPoints.add(new DustDataPoint(headerNames, fieldsConverted));
        }

        return dataPoints;
    }

    public static DataSource<DustDataPoint> generateRandomData(Integer size) {
        List<DustDataPoint> dataPoints = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            dataPoints.add(generateRandomTuple());
        }

        return new StreamDataSource<>(dataPoints.stream());
    }

    protected static DustDataPoint generateRandomTuple() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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

        double randomError = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        if (randomError > 0.00 && randomError < 0.01) {
            smallParticles = null;
        } else if (randomError > 0.01 && randomError < 0.02) {
            largeParticles = null;
        } else if (randomError > 0.02 && randomError < 0.03) {
            temperature = null;
        } else if (randomError > 0.03 && randomError < 0.04) {
            humidity = null;
        }

        return new DustDataPoint(date, smallParticles, largeParticles, humidity, temperature);
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
    public List<T> collect() {
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

    @Override
    public long profile() {
        long currentTime = System.nanoTime();
        collect();
        return System.nanoTime() - currentTime;
    }

    public Stream<T> stream() {
        return this.wrappedStream;
    }
}
