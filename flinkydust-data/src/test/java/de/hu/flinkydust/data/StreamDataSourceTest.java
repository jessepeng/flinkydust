package de.hu.flinkydust.data;

import de.hu.flinkydust.data.aggregator.AvgAggregator;
import de.hu.flinkydust.data.aggregator.MaxAggregator;
import de.hu.flinkydust.data.aggregator.MinAggregator;
import de.hu.flinkydust.data.aggregator.TimeWindowAggregator;
import de.hu.flinkydust.data.comparator.AtLeastComparator;
import de.hu.flinkydust.data.comparator.LessThanComparator;
import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.projector.FieldnameProjector;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by Jan-Christopher on 09.11.2016.
 */
public class StreamDataSourceTest {

    @Test
    public void testMinAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DustDataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DustDataPoint> minLarge = dataSource.aggregation(new MinAggregator<>("Large", Double.class));

        System.out.println("MinAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DustDataPoint> minList = minLarge.collect();
        timeAfter = System.nanoTime();

        Assert.assertThat(minList.size(), Is.is(1));
        Assert.assertThat(minList.get(0).getDoubleField("Large"), Is.is(-161480.0));
        System.out.println("MinAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testMaxAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DustDataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DustDataPoint> maxSmall = dataSource.aggregation(new MaxAggregator<>("Small", Double.class));

        System.out.println("MaxAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DustDataPoint> minList = maxSmall.collect();
        timeAfter = System.nanoTime();

        Assert.assertThat(minList.size(), Is.is(1));
        Assert.assertThat(minList.get(0).getDoubleField("Small"), Is.is(1537877.0));
        System.out.println("MaxAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testSelection() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DustDataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DustDataPoint> selected = dataSource.selection(new AtLeastComparator<>("Small", 100.0, Double.class));

        System.out.println("Selection Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DustDataPoint> selectedList = selected.collect();
        timeAfter = System.nanoTime();

        Assert.assertThat(selectedList.size(), Is.is(409160));
        System.out.println("Selection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testProjection() throws Exception {
        String[] projectionTarget = {"MasterTime","Small"};
        DataSource<DustDataPoint> projected = StreamDataSource.readFile("data/dust-2014.dat").projection(new FieldnameProjector(projectionTarget));

        String[] projectionTarget2 = {"Large","RelHumidity"};
        DataSource<DustDataPoint> projected2 = StreamDataSource.readFile("data/dust-2014.dat").projection(new FieldnameProjector(projectionTarget2));

        long timeBefore, timeAfter;
        timeBefore = System.nanoTime();
        List<DustDataPoint> selectedList = projected.collect();

        List<DustDataPoint> selectedList2 = projected2.collect();
        timeAfter = System.nanoTime();

        System.out.println("Projection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
        System.out.println(selectedList.get(0));
        System.out.println(selectedList2.get(0));

        Assert.assertThat(selectedList.get(0).getDoubleField("Small"), Is.is(3680.0));
        try {
            selectedList.get(0).getDoubleField("Large");
            fail("Erwartete Exception wurde nicht geworfen.");
        } catch (IllegalArgumentException e) {

        }

        Assert.assertThat(selectedList2.get(0).getDoubleField("Large"), Is.is(10.0));
        try {
            selectedList2.get(0).getDoubleField("Small");
            fail("Erwartete Exception wurde nicht geworfen.");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testAvgAggregation() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DustDataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();
        DataSource<DustDataPoint> avgSmall = dataSource.aggregation(new AvgAggregator("Small"));

        System.out.println("AvgAggregation Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DustDataPoint> avgList = avgSmall.collect();
        timeAfter = System.nanoTime();

        Assert.assertThat(avgList.size(), Is.is(1));
        Assert.assertThat(avgList.get(0).getDoubleField("Small"), Is.is(27961.975451166792));
        System.out.println("AvgAggregation: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    public void testMultipleOperators() throws Exception {
        long timeBefore = System.nanoTime();
        DataSource<DustDataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");
        long timeAfter = System.nanoTime();

        DataSource<DustDataPoint> multipleOperatorsDataSource = dataSource
                .selection(new AtLeastComparator<>("Small", 100.0, Double.class))
                .selection(new LessThanComparator<>("Small", 500.0, Double.class))
                .aggregation(new MaxAggregator<>("Large", Double.class));

        System.out.println("Multiple Operators Read File: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

        timeBefore = System.nanoTime();
        List<DustDataPoint> list = multipleOperatorsDataSource.collect();
        timeAfter = System.nanoTime();

        Assert.assertThat(list.size(), Is.is(1));
        Assert.assertThat(list.get(0).getDoubleField("Large"), Is.is(150.0));
        System.out.println("Multiple Operators: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));
    }

    @Test
    @Ignore
    public void testProfileRandomNumbers() throws Exception {
        long timeBefore10k = System.nanoTime();
        DataSource<DustDataPoint> dataSource1 = StreamDataSource.generateRandomData(1000);
        DataSource<DustDataPoint> selected1 = dataSource1.selection(new AtLeastComparator<>("small", 100.0, Double.class));

        dataSource1 = StreamDataSource.generateRandomData(1000);
        String[] projectionTarget = {"small"};
        DataSource<DustDataPoint> projection1 = dataSource1.projection(new FieldnameProjector(projectionTarget));

        dataSource1 = StreamDataSource.generateRandomData(1000);
        DataSource<DustDataPoint> maxSmall1 = dataSource1.aggregation(new MaxAggregator<>("small", Double.class));

        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to generate " + 10000 + " random DustDataPoint objects and select, project and aggregate: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));

        long timeBefore100k = System.nanoTime();
        DataSource<DustDataPoint> dataSource2 = StreamDataSource.generateRandomData(10000);
        DataSource<DustDataPoint> selected2 = dataSource2.selection(new AtLeastComparator<>("small", 100.0, Double.class));

        dataSource2 = StreamDataSource.generateRandomData(10000);
        DataSource<DustDataPoint> projection2 = dataSource2.projection(new FieldnameProjector(projectionTarget));

        dataSource2 = StreamDataSource.generateRandomData(10000);
        DataSource<DustDataPoint> maxSmall2 = dataSource2.aggregation(new MaxAggregator<>("small", Double.class));
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to generate " + 100000 + " random DustDataPoint objects and select, project and aggregate: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));


        long timeBefore1000k = System.nanoTime();
        DataSource<DustDataPoint> dataSource3 = StreamDataSource.generateRandomData(100000);
        DataSource<DustDataPoint> selected3 = dataSource3.selection(new AtLeastComparator<>("small", 100.0, Double.class));

        dataSource3 = StreamDataSource.generateRandomData(100000);
        DataSource<DustDataPoint> projection3 = dataSource3.projection(new FieldnameProjector(projectionTarget));

        dataSource3 = StreamDataSource.generateRandomData(100000);
        DataSource<DustDataPoint> maxSmall3 = dataSource3.aggregation(new MaxAggregator<>("small", Double.class));
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to generate " + 1000000 + " random DustDataPoint objects and select, project and aggregate: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }

    @Test
    @Ignore
    public void testProfile() throws Exception {
        long cumulatedTime = 0;
        System.out.println("Running profile (10 times 1.000.000 data points: ");
        for (int i = 0; i < 10; i++) {
            DataSource<DustDataPoint> dataSource = StreamDataSource.generateRandomData(1000000);

            long timeBefore = System.nanoTime();
            dataSource.selection(new AtLeastComparator<>("small", 100.0, Double.class)).collect();
            long timeAfter = System.nanoTime();

            System.out.println("Selection: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

            cumulatedTime += timeAfter - timeBefore;
        }

        System.out.println("Average run for 1.000.000 dataPoints: " + (cumulatedTime / 10 / 1000000000.0));

//        cumulatedTime = 0;
//        System.out.println("Running profile (10 times 10.000.000 data points, will take a long time!: ");
//        for (int i = 0; i < 10; i++) {
//            DataSource<DustDataPoint> dataSource = StreamDataSource.generateRandomData(10000000);
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
    @Ignore
    public void testProfileRandomNumbersSelection() throws Exception {
        DataSource<DustDataPoint> dataSource1 = StreamDataSource.generateRandomData(10000);
        long timeBefore10k = System.nanoTime();
        dataSource1.selection(new AtLeastComparator<>("Small", 100.0, Double.class)).collect();
        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to select " + 10000 + " random DustDataPoint objects: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));

        DataSource<DustDataPoint> dataSource2 = StreamDataSource.generateRandomData(100000);
        long timeBefore100k = System.nanoTime();
        dataSource2.selection(new AtLeastComparator<>("Small", 100.0, Double.class)).collect();
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to select " + 100000 + " random DustDataPoint objects: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));

        DataSource<DustDataPoint> dataSource3 = StreamDataSource.generateRandomData(1000000);
        long timeBefore1000k = System.nanoTime();
        dataSource3.selection(new AtLeastComparator<>("Small", 100.0, Double.class)).collect();
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to select " + 1000000 + " random DustDataPoint objects: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }

    @Test
    @Ignore
    public void testProfileRandomNumbersAggregation() throws Exception {
        DataSource<DustDataPoint> dataSource1 = StreamDataSource.generateRandomData(10000);
        long timeBefore10k = System.nanoTime();
        dataSource1.aggregation(new MaxAggregator<>("Small", Double.class)).collect();
        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to aggregate " + 10000 + " random DustDataPoint objects: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));


        DataSource<DustDataPoint> dataSource2 = StreamDataSource.generateRandomData(100000);
        long timeBefore100k = System.nanoTime();
        dataSource2.aggregation(new MaxAggregator<>("Small", Double.class)).collect();
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to aggregate " + 100000 + " random DustDataPoint objects: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));


        DataSource<DustDataPoint> dataSource3 = StreamDataSource.generateRandomData(1000000);
        long timeBefore1000k = System.nanoTime();
        dataSource3.aggregation(new MaxAggregator<>("Small", Double.class)).collect();
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to aggregate " + 1000000 + " random DustDataPoint objects: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }

    @Test
    @Ignore
    public void testProfileRandomNumbersProjection() throws Exception {
        String[] projectionTarget = {"small"};

        DataSource<DustDataPoint> dataSource1 = StreamDataSource.generateRandomData(10000);
        long timeBefore10k = System.nanoTime();
        dataSource1.projection(new FieldnameProjector(projectionTarget)).collect();
        long timeAfter10k = System.nanoTime();

        System.out.println("The time in s to project " + 10000 + " random DustDataPoint objects: " + String.valueOf((timeAfter10k - timeBefore10k) / 1000000000.0));

        DataSource<DustDataPoint> dataSource2 = StreamDataSource.generateRandomData(100000);
        long timeBefore100k = System.nanoTime();
        dataSource2.projection(new FieldnameProjector(projectionTarget)).collect();
        long timeAfter100k = System.nanoTime();

        System.out.println("The time in s to project " + 100000 + " random DustDataPoint objects: " + String.valueOf((timeAfter100k - timeBefore100k) / 1000000000.0));

        DataSource<DustDataPoint> dataSource3 = StreamDataSource.generateRandomData(1000000);
        long timeBefore1000k = System.nanoTime();
        dataSource3.projection(new FieldnameProjector(projectionTarget)).collect();
        long timeAfter1000k = System.nanoTime();

        System.out.println("The time in s to project " + 1000000 + " random DustDataPoint objects: " + String.valueOf((timeAfter1000k - timeBefore1000k) / 1000000000.0));
    }

    @Test
    public void testTimeWindowAggregation() throws Exception {
        DataSource<DustDataPoint> dataSource = StreamDataSource.readFile("data/dust-2014.dat");

        long timeBefore = System.nanoTime();
        DataSource<DustDataPoint> aggregatedDataSource = dataSource.aggregation(new TimeWindowAggregator(6));
        long timeAfter = System.nanoTime();

        System.out.println("The time for windowing time aggregatio: " + ((timeAfter - timeBefore) / 1000000000.0));

        Assert.assertThat(aggregatedDataSource.stream().count(), Is.is(1459L));
    }
}