package de.hu.flinkydust.data;

import de.hu.flinkydust.data.aggregator.TupleMaxAggregator;
import de.hu.flinkydust.data.aggregator.TupleMinAggregator;
import de.hu.flinkydust.data.comparator.AtLeastComparator;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple5;
import org.hamcrest.core.Is;
import org.junit.Test;

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
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        System.out.println("MinAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> minLarge = dataSource.aggregation(new TupleMinAggregator<>(2, Integer.class));
        timeAfter = System.nanoTime();
        System.out.println("MinAggregation Operation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Integer, Integer, Float, Float>> minList = minLarge.collect();
        timeAfter = System.nanoTime();
        System.out.println("MinAggregation Convert to List: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        assertThat(minList.size(), Is.is(1));
        assertThat(minList.get(0).f2, Is.is(-161480));
    }

    @Test
    public void testMaxAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        System.out.println("MaxAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> maxSmall = dataSource.aggregation(new TupleMaxAggregator<>(1, Integer.class));
        timeAfter = System.nanoTime();
        System.out.println("MaxAggregation Operation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
        maxSmall.print();

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Integer, Integer, Float, Float>> minList = maxSmall.collect();
        timeAfter = System.nanoTime();
        System.out.println("Selection Convert to List: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        assertThat(minList.size(), Is.is(1));
        assertThat(minList.get(0).f1, Is.is(1537877));
    }

    @Test
    public void testSelection() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        System.out.println("Selection Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> selected = dataSource.selection(new AtLeastComparator<>(1, 100, Integer.class));
        timeAfter = System.nanoTime();
        System.out.println("Selection Operation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<Tuple5<Date, Integer, Integer, Float, Float>> selectedList = selected.collect();
        timeAfter = System.nanoTime();
        System.out.println("Selection Convert to List: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        assertThat(selectedList.size(), Is.is(409216));
    }

}