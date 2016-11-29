package de.hu.flinkydust.test;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.StreamDataSource;
import de.hu.flinkydust.data.aggregator.AvgAggregator;
import de.hu.flinkydust.data.aggregator.MaxAggregator;
import de.hu.flinkydust.data.aggregator.MinAggregator;
import de.hu.flinkydust.data.comparator.AtLeastComparator;
import de.hu.flinkydust.data.comparator.LessThanComparator;
import de.hu.flinkydust.data.projector.FieldnameProjector;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThat;
import java.util.Optional;

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

    @Test
    public void testProjection() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();

        String[] projectionTarget = {"date","small"};
        DataSource<DataPoint> projected = dataSource.projection(new FieldnameProjector(projectionTarget));

        String[] projectionTarget2 = {"large","relHumid"};
        dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        DataSource<DataPoint> projected2 = dataSource.projection(new FieldnameProjector(projectionTarget2));

        System.out.println("Selection Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DataPoint> selectedList = projected.collect();

        List<DataPoint> selectedList2 = projected2.collect();
        timeAfter = System.nanoTime();

        System.out.println("Selection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
        System.out.println(selectedList.get(0));
        System.out.println(selectedList2.get(0));

        assertThat(selectedList.get(0).getSmall(), Is.is(3680.0));
        assertThat(selectedList.get(0).getLarge(), Is.is(0.0));

        assertThat(selectedList2.get(0).getLarge(), Is.is(10.0));
        assertThat(selectedList2.get(0).getSmall(), Is.is(0.0));
    }

    @Test
    public void testAvgAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DataPoint> avgSmall = dataSource.aggregation(new AvgAggregator<>("small", Double.class));

        System.out.println("AvgAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DataPoint> avgList = avgSmall.collect();
        timeAfter = System.nanoTime();

        assertThat(avgList.size(), Is.is(1));
        assertThat(avgList.get(0).f1, Is.is(27961.975451166792));
        System.out.println("AvgAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

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

    @Test
    public void testProfileRandomNumbers() throws Exception {
        long timeBefore10k = System.nanoTime();
        DataSource<DataPoint> dataSource1 = StreamDataSource.generateRandomData(1000);
        DataSource<DataPoint> selected1 = dataSource1.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class));

        dataSource1 = StreamDataSource.generateRandomData(1000);
        String[] projectionTarget = {"small"};
        DataSource<DataPoint> projection1 = dataSource1.projection(new FieldnameProjector(projectionTarget));

        dataSource1 = StreamDataSource.generateRandomData(1000);
        DataSource<DataPoint> maxSmall1 = dataSource1.aggregation(new MaxAggregator<>("small", 0.0, Double.class));

        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to generate " + 10000 + " random DataPoint objects and select, project and aggregate: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));

        long timeBefore100k = System.nanoTime();
        DataSource<DataPoint> dataSource2 = StreamDataSource.generateRandomData(10000);
        DataSource<DataPoint> selected2 = dataSource2.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class));

        dataSource2 = StreamDataSource.generateRandomData(10000);
        DataSource<DataPoint> projection2 = dataSource2.projection(new FieldnameProjector(projectionTarget));

        dataSource2 = StreamDataSource.generateRandomData(10000);
        DataSource<DataPoint> maxSmall2 = dataSource2.aggregation(new MaxAggregator<>("small", 0.0, Double.class));
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to generate " + 100000 + " random DataPoint objects and select, project and aggregate: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));


        long timeBefore1000k = System.nanoTime();
        DataSource<DataPoint> dataSource3 = StreamDataSource.generateRandomData(100000);
        DataSource<DataPoint> selected3 = dataSource3.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class));

        dataSource3 = StreamDataSource.generateRandomData(100000);
        DataSource<DataPoint> projection3 = dataSource3.projection(new FieldnameProjector(projectionTarget));

        dataSource3 = StreamDataSource.generateRandomData(100000);
        DataSource<DataPoint> maxSmall3 = dataSource3.aggregation(new MaxAggregator<>("small", 0.0, Double.class));
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to generate " + 1000000 + " random DataPoint objects and select, project and aggregate: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }

    @Test
    @Ignore
    public void testProfile() throws Exception {
        long cumulatedTime = 0;
        System.out.println("Running profile (10 times 1.000.000 data points: ");
        for (int i = 0; i < 10; i++) {
            DataSource<DataPoint> dataSource = StreamDataSource.generateRandomData(1000000);

            long timeBefore = System.nanoTime();
            dataSource.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class)).collect();
            long timeAfter = System.nanoTime();

            System.out.println("Selection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

            cumulatedTime += timeAfter - timeBefore;
        }

        System.out.println("Average run for 1.000.000 dataPoints: " + (cumulatedTime / 10 / 1000000000.0));

//        cumulatedTime = 0;
//        System.out.println("Running profile (10 times 10.000.000 data points, will take a long time!: ");
//        for (int i = 0; i < 10; i++) {
//            DataSource<DataPoint> dataSource = StreamDataSource.generateRandomData(10000000);
//
//            long timeBefore = System.nanoTime();
//            dataSource.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class)).collect();
//            long timeAfter = System.nanoTime();
//
//            System.out.println("Selection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
//
//            cumulatedTime += timeAfter - timeBefore;
//        }
//
//        System.out.println("Average run for 10.000.000 dataPoints: " + (cumulatedTime / 10 / 1000000000.0));
    }

    @Test
    public void testProfileRandomNumbersSelection() throws Exception {
        DataSource<DataPoint> dataSource1 = StreamDataSource.generateRandomData(10000);
        long timeBefore10k = System.nanoTime();
        dataSource1.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class)).collect();
        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to select " + 10000 + " random DataPoint objects: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));

        DataSource<DataPoint> dataSource2 = StreamDataSource.generateRandomData(100000);
        long timeBefore100k = System.nanoTime();
        dataSource2.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class)).collect();
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to select " + 100000 + " random DataPoint objects: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));

        DataSource<DataPoint> dataSource3 = StreamDataSource.generateRandomData(1000000);
        long timeBefore1000k = System.nanoTime();
        dataSource3.selection(new AtLeastComparator<>("small", 100.0, 0.0, Double.class)).collect();
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to select " + 1000000 + " random DataPoint objects: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }

    @Test
    public void testProfileRandomNumbersAggregation() throws Exception {
        DataSource<DataPoint> dataSource1 = StreamDataSource.generateRandomData(10000);
        long timeBefore10k = System.nanoTime();
        dataSource1.aggregation(new MaxAggregator<>("small", 0.0, Double.class)).collect();
        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to aggregate " + 10000 + " random DataPoint objects: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));


        DataSource<DataPoint> dataSource2 = StreamDataSource.generateRandomData(100000);
        long timeBefore100k = System.nanoTime();
        dataSource2.aggregation(new MaxAggregator<>("small", 0.0, Double.class)).collect();
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to aggregate " + 100000 + " random DataPoint objects: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));


        DataSource<DataPoint> dataSource3 = StreamDataSource.generateRandomData(1000000);
        long timeBefore1000k = System.nanoTime();
        dataSource3.aggregation(new MaxAggregator<>("small", 0.0, Double.class)).collect();
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to aggregate " + 1000000 + " random DataPoint objects: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }

    @Test
    public void testProfileRandomNumbersProjection() throws Exception {
        String[] projectionTarget = {"small"};

        DataSource<DataPoint> dataSource1 = StreamDataSource.generateRandomData(10000);
        long timeBefore10k = System.nanoTime();
        dataSource1.projection(new FieldnameProjector(projectionTarget)).collect();
        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to project " + 10000 + " random DataPoint objects: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));

        DataSource<DataPoint> dataSource2 = StreamDataSource.generateRandomData(100000);
        long timeBefore100k = System.nanoTime();
        dataSource2.projection(new FieldnameProjector(projectionTarget)).collect();
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to project " + 100000 + " random DataPoint objects: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));

        DataSource<DataPoint> dataSource3 = StreamDataSource.generateRandomData(1000000);
        long timeBefore1000k = System.nanoTime();
        dataSource3.projection(new FieldnameProjector(projectionTarget)).collect();
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to project " + 1000000 + " random DataPoint objects: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }
}