package de.hu.flinkydust.profile;

import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.StreamDataSource;
import de.hu.flinkydust.data.aggregator.AggregatorFunction;
import de.hu.flinkydust.data.aggregator.AvgAggregator;
import de.hu.flinkydust.data.aggregator.MaxAggregator;
import de.hu.flinkydust.data.aggregator.MinAggregator;
import de.hu.flinkydust.data.comparator.AtLeastComparator;
import de.hu.flinkydust.data.comparator.LessThanComparator;
import de.hu.flinkydust.data.comparator.SameComparator;
import de.hu.flinkydust.data.projector.FieldnameProjector;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by Jan-Christopher on 28.11.2016.
 */
public class FlinkydustProfile {

    public static void main(String[] args) {
        if (args.length < 3) {
            printInstructions();
            return;
        }

        String command = args[2].trim();
        int noOfDatasets = Integer.valueOf(args[0]);
        int iterations = Integer.valueOf(args[1]);

        switch (command) {
            case "aggregation":
                if (args.length != 5) {
                    printInstructions();
                    return;
                }
                break;
            case "projection":
                if (args.length < 4) {
                    printInstructions();
                    return;
                }
                break;
            case "selection":
                if (args.length != 6) {
                    printInstructions();
                    return;
                }
                break;
        }

        long runTimeWhole = 0;
        for (int iteration = 1; iteration <= iterations; iteration++) {
            System.out.println("Generating " + noOfDatasets + " data points of random data...");
            DataSource<DustDataPoint> dataSource = StreamDataSource.generateRandomData(noOfDatasets);
            System.out.println("Finished generating random data. Running command " + command);
            long runTime = 0;
            switch (command) {
                case "aggregation":
                    AggregatorFunction<DustDataPoint> aggregator = null;
                    switch (args[3]) {
                        case "min":
                            if (args[4].equals("date")) {
                                aggregator = new MinAggregator<>(args[4], Date.class);
                            } else {
                                aggregator = new MinAggregator<>(args[4], Double.class);
                            }
                            break;
                        case "max":
                            if (args[4].equals("date")) {
                                aggregator = new MaxAggregator<>(args[4], Date.class);
                            } else {
                                aggregator = new MaxAggregator<>(args[4], Double.class);
                            }
                            break;
                        case "avg":
                            if (args[4].equals("date")) {
                                System.err.println("Can't average aggregate using field 'date'");
                                return;
                            } else {
                                aggregator = new AvgAggregator(args[4]);
                            }
                    }
                    runTime = dataSource.aggregation(aggregator).profile();
                    break;
                case "projection":
                    String[] fields = new String[args.length - 3];
                    System.arraycopy(args, 3, fields, 0, args.length - 3);
                    runTime = dataSource.projection(new FieldnameProjector(fields)).profile();
                    break;
                case "selection":
                    Predicate<DustDataPoint> predicate = null;
                    switch (args[3]) {
                        case "atLeast":
                            if (args[4].equals("date")) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date date;
                                try {
                                    date = dateFormat.parse(args[5]);
                                } catch (ParseException e) {
                                    printInstructions();
                                    return;
                                }
                                predicate = new AtLeastComparator<>(args[4], date, Date.class);
                            } else {
                                predicate = new AtLeastComparator<>(args[4], Double.valueOf(args[5]), Double.class);
                            }
                            break;
                        case "lessThan":
                            if (args[4].equals("date")) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date date = null;
                                try {
                                    date = dateFormat.parse(args[5]);
                                } catch (ParseException e) {
                                    printInstructions();
                                    return;
                                }
                                predicate = new LessThanComparator<>(args[4], date, Date.class);
                            } else {
                                predicate = new LessThanComparator<>(args[4], Double.valueOf(args[5]), Double.class);
                            }
                            break;
                        case "same":
                            if (args[4].equals("date")) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date date = null;
                                try {
                                    date = dateFormat.parse(args[5]);
                                } catch (ParseException e) {
                                    printInstructions();
                                    return;
                                }
                                predicate = new SameComparator<>(args[4], date, Date.class);
                            } else {
                                predicate = new SameComparator<>(args[4], Double.valueOf(args[5]), Double.class);
                            }
                            break;
                    }
                    runTime = dataSource.selection(predicate).profile();
                    break;
            }
            System.out.println("Running time for iteration " + iteration + ": " + runTime / 1000000000.0 + "s.");
            runTimeWhole += runTime;
        }
        System.out.println("Average running time for " + iterations + " iterations: " + runTimeWhole / iterations / 1000000000.0 + "s.");
    }

    private static void printInstructions() {
        String[] output = {
                "Usage: profile.sh <no of items> <no of iterations> <function> [<function arg1> <function arg2>]" + System.lineSeparator(),
                "where <function> is one of:" + System.lineSeparator(),
                "\t'aggregation'" + System.lineSeparator(),
                "\t'projection'" + System.lineSeparator(),
                "\t'selection'" + System.lineSeparator(),
                System.lineSeparator(),
                "Arguments for function 'aggregation':" + System.lineSeparator(),
                "<aggregator>: One of 'min', 'max', 'avg'"  + System.lineSeparator(),
                "<field>: One of 'date', 'small', 'large', 'relHumidity', 'temp'"  + System.lineSeparator(),
                System.lineSeparator(),
                "Arguments for function 'projection':"  + System.lineSeparator(),
                "<field1>: First field to project on" + System.lineSeparator(),
                "<field2>: Second field to project on" + System.lineSeparator(),
                "... other fields to project on" + System.lineSeparator(),
                System.lineSeparator(),
                "Arguments for function 'selection':"  + System.lineSeparator(),
                "<selector>: One of 'atLeast', 'lessThan', 'same'" + System.lineSeparator(),
                "<field>: Field to select on" + System.lineSeparator(),
                "<compareValue>: Value to compare to (date format: yyyy-MM-dd hh:mm:ss)"
        };
        Stream.of(output).forEach(System.err::println);
    }

}
