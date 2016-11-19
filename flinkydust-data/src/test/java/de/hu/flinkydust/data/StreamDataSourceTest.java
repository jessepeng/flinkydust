package de.hu.flinkydust.data;

import de.hu.flinkydust.data.aggregator.MaxAggregator;
import de.hu.flinkydust.data.aggregator.MinAggregator;
import de.hu.flinkydust.data.comparator.AtLeastComparator;
import de.hu.flinkydust.data.comparator.LessThanComparator;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThat;

/**
 * Created by Jan-Christopher on 09.11.2016.
 */
public class StreamDataSourceTest {

    @Test
    public void testMinAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DataPoint> minLarge = dataSource.aggregation(new MinAggregator<>("large", 0.0, Double.class));

        System.out.println("MinAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DataPoint> minList = minLarge.collect();
        timeAfter = System.nanoTime();

        assertThat(minList.size(), Is.is(1));
        assertThat(minList.get(0).getLarge(), Is.is(-161480.0));
        System.out.println("MinAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testMaxAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DataPoint> maxSmall = dataSource.aggregation(new MaxAggregator<>("small", 0.0, Double.class));

        System.out.println("MaxAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DataPoint> minList = maxSmall.collect();
        timeAfter = System.nanoTime();

        assertThat(minList.size(), Is.is(1));
        assertThat(minList.get(0).getSmall(), Is.is(1537877.0));
        System.out.println("MaxAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testSelection() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DataPoint> selected = dataSource.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class));

        System.out.println("Selection Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DataPoint> selectedList = selected.collect();
        timeAfter = System.nanoTime();

        assertThat(selectedList.size(), Is.is(409216));
        System.out.println("Selection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

//    @Test
//    public void testAvgAggregation() throws Exception {
//        long timeBefore = System.nanoTime();
//        DataSource<DataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
//        long timeAfter = System.nanoTime();
//        DataSource<DataPoint> avgSmall = dataSource.aggregation(new AvgAggregator<>(1, Double.class));
//
//        System.out.println("AvgAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
//
//        timeBefore = System.nanoTime();
//        List<DataPoint> avgList = avgSmall.collect();
//        timeAfter = System.nanoTime();
//
//        assertThat(avgList.size(), Is.is(1));
//        assertThat(avgList.get(0).f1, Is.is(27961.975451166792));
//        System.out.println("AvgAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
//    }

    @Test
    public void testMultipleOperators() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();

        DataSource<DataPoint> multipleOperatorsDataSource = dataSource
                .selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class))
                .selection(new LessThanComparator<>("small", 500.0, 0.0, Double.class))
                .aggregation(new MaxAggregator<>("large", 0.0, Double.class));

        System.out.println("Multiple Operators Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DataPoint> list = multipleOperatorsDataSource.collect();
        timeAfter = System.nanoTime();

        assertThat(list.size(), Is.is(1));
        assertThat(list.get(0).getLarge(), Is.is(165.0));
        System.out.println("Multiple Operators: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

}