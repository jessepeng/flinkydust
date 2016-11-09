package de.hu.flinkydust.data;

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
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> minLarge = dataSource.aggregation(new TupleMinAggregator<Tuple5<Date, Integer, Integer, Float, Float>, Integer>(2, Integer.class));

        List<Tuple5<Date, Integer, Integer, Float, Float>> minList = minLarge.collect();

        assertThat(minList.size(), Is.is(1));
        assertThat(minList.get(0).f2, Is.is(-161480));
    }

    @Test
    public void testSelection() throws Exception {
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> dataSource = DataSetDataSource.readFile(executionEnvironment, "data/dust-2014.dat");
        DataSource<Tuple5<Date, Integer, Integer, Float, Float>> selected = dataSource.selection(new AtLeastComparator<>(1, 100, Integer.class));

        long timeBefore = System.nanoTime();
        List<Tuple5<Date, Integer, Integer, Float, Float>> selectedList = selected.collect();
        long timeAfter = System.nanoTime();

        assertThat(selectedList.size(), Is.is(409216));
        System.out.println("Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

}