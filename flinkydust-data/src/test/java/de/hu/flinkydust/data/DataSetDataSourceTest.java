package de.hu.flinkydust.data;

import de.hu.flinkydust.data.aggregator.TupleAvgAggregator;
import de.hu.flinkydust.data.aggregator.TupleMaxAggregator;
import de.hu.flinkydust.data.aggregator.TupleMinAggregator;
import de.hu.flinkydust.data.comparator.AtLeastComparator;
import de.hu.flinkydust.data.comparator.LessThanComparator;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.streaming.runtime.tasks.ExceptionInChainedOperatorException;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jan-Christopher on 09.11.2016.
 */
public class DataSetDataSourceTest {

    private ExecutionEnvironment executionEnvironment;

    @org.junit.Before
    public void setUp() throws Exception {
        executionEnvironment = ExecutionEnvironment.createCollectionsEnvironment();
    }

    @Test
    public void testMinAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> minLarge = dataSource.aggregation(new TupleMinAggregator<>(2, Double.class));

        System.out.println("MinAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Double, Double, Double, Double>> minList = minLarge.collect();
        timeAfter = System.nanoTime();

        assertThat(minList.size(), Is.is(1));
        assertThat(minList.get(0).f2, Is.is(-161480.0));
        System.out.println("MinAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testMaxAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> maxSmall = dataSource.aggregation(new TupleMaxAggregator<>(1, Double.class));

        System.out.println("MaxAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Double, Double, Double, Double>> minList = maxSmall.collect();
        timeAfter = System.nanoTime();

        assertThat(minList.size(), Is.is(1));
        assertThat(minList.get(0).f1, Is.is(1537877.0));
        System.out.println("MaxAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testSelection() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> selected = dataSource.selection(new AtLeastComparator<>(1, 100.0, Double.class));

        System.out.println("Selection Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Double, Double, Double, Double>> selectedList = selected.collect();
        timeAfter = System.nanoTime();

        assertThat(selectedList.size(), Is.is(409216));
        System.out.println("Selection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testAvgAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> avgSmall = dataSource.aggregation(new TupleAvgAggregator<>(1, Double.class));

        System.out.println("AvgAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Double, Double, Double, Double>> avgList = avgSmall.collect();
        timeAfter = System.nanoTime();

        assertThat(avgList.size(), Is.is(1));
        assertThat(avgList.get(0).f1, Is.is(27961.98));
        System.out.println("AvgAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testMultipleOperators() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Double, Double, Double, Double>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();

        DataSource<Tuple5<Date, Double, Double, Double, Double>> multipleOperatorsDataSource = dataSource
                .selection(new AtLeastComparator<>(1, 100.0, Double.class))
                .selection(new LessThanComparator<>(1, 500.0, Double.class))
                .aggregation(new TupleMaxAggregator<>(2, Double.class));

        System.out.println("Multiple Operators Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Double, Double, Double, Double>> list = multipleOperatorsDataSource.collect();
        timeAfter = System.nanoTime();

        assertThat(list.size(), Is.is(1));
        assertThat(list.get(0).f2, Is.is(165.0));
        System.out.println("Multiple Operators: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

}