package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceMeasurableDataPoint;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;

/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDistanceClusterTest {

    @Test
    public void testCentroid() {
        EuclidianDistanceMeasurableDataPoint dataPoint1 = new BasicEuclidianDistanceDataPoint(-1.0, 10.0, 3.0);
        EuclidianDistanceMeasurableDataPoint dataPoint2 = new BasicEuclidianDistanceDataPoint(0.0, 5.0, 2.0);
        EuclidianDistanceMeasurableDataPoint dataPoint3 = new BasicEuclidianDistanceDataPoint(1.0, 20.0, 10.0);

        List<EuclidianDistanceMeasurableDataPoint> dataPointList = new ArrayList<>();
        dataPointList.addAll(Arrays.asList(dataPoint1, dataPoint2, dataPoint3));

        Cluster<EuclidianDistanceMeasurableDataPoint> cluster = new EuclidianDistanceCluster(dataPointList);

        EuclidianDistanceMeasurableDataPoint centroid = cluster.getCentroid();
        assertThat(centroid.getDimension(0), Is.is(0.0));
        assertThat(centroid.getDimension(1), Is.is(11.0 + 2.0 / 3.0));
        assertThat(centroid.getDimension(2), Is.is(5.0));
    }

    @Test
    public void testAddCentroid() {
        EuclidianDistanceMeasurableDataPoint dataPoint1 = new BasicEuclidianDistanceDataPoint(-1.0, 10.0, 3.0);
        EuclidianDistanceMeasurableDataPoint dataPoint2 = new BasicEuclidianDistanceDataPoint(0.0, 5.0, 2.0);
        EuclidianDistanceMeasurableDataPoint dataPoint3 = new BasicEuclidianDistanceDataPoint(1.0, 20.0, 10.0);

        Cluster<EuclidianDistanceMeasurableDataPoint> cluster = new EuclidianDistanceCluster(dataPoint1);
        EuclidianDistanceMeasurableDataPoint centroid = cluster.getCentroid();
        assertThat(centroid.getDimension(0), Is.is(-1.0));
        assertThat(centroid.getDimension(1), Is.is(10.0));
        assertThat(centroid.getDimension(2), Is.is(3.0));

        Cluster<EuclidianDistanceMeasurableDataPoint> cluster2 = new EuclidianDistanceCluster(Arrays.asList(dataPoint2, dataPoint3));

        Cluster<EuclidianDistanceMeasurableDataPoint> resultingCluster = cluster.merge(cluster2);

        centroid = resultingCluster.getCentroid();
        assertThat(centroid.getDimension(0), Is.is(0.0));
        assertThat(centroid.getDimension(1), Is.is(11.0 + 2.0 / 3.0));
        assertThat(centroid.getDimension(2), Is.is(5.0));
    }

}