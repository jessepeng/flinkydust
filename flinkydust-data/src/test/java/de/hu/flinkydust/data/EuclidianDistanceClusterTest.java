package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;
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
        EuclidianDistanceDataPoint dataPoint1 = new BasicEuclidianDistanceDataPoint(-1.0, 10.0, 3.0);
        EuclidianDistanceDataPoint dataPoint2 = new BasicEuclidianDistanceDataPoint(0.0, 5.0, 2.0);
        EuclidianDistanceDataPoint dataPoint3 = new BasicEuclidianDistanceDataPoint(1.0, 20.0, 10.0);

        List<EuclidianDistanceDataPoint> dataPointList = new ArrayList<>();
        dataPointList.addAll(Arrays.asList(dataPoint1, dataPoint2, dataPoint3));

        Cluster<EuclidianDistanceDataPoint> cluster = new EuclidianDistanceCluster<>(dataPointList);

        EuclidianDistanceDataPoint centroid = cluster.getCentroid();
        assertThat(centroid.getDimension(0), Is.is(0.0));
        assertThat(centroid.getDimension(1), Is.is(11.0 + 2.0 / 3.0));
        assertThat(centroid.getDimension(2), Is.is(5.0));
    }

    @Test
    public void testAddCentroid() {
        EuclidianDistanceDataPoint dataPoint1 = new BasicEuclidianDistanceDataPoint(-1.0, 10.0, 3.0);
        EuclidianDistanceDataPoint dataPoint2 = new BasicEuclidianDistanceDataPoint(0.0, 5.0, 2.0);
        EuclidianDistanceDataPoint dataPoint3 = new BasicEuclidianDistanceDataPoint(1.0, 20.0, 10.0);

        Cluster<EuclidianDistanceDataPoint> cluster = new EuclidianDistanceCluster<>(dataPoint1);

        EuclidianDistanceDataPoint centroid = cluster.getCentroid();
        assertThat(centroid.getDimension(0), Is.is(-1.0));
        assertThat(centroid.getDimension(1), Is.is(10.0));
        assertThat(centroid.getDimension(2), Is.is(3.0));

        Cluster<EuclidianDistanceDataPoint> cluster2 = new EuclidianDistanceCluster<>(Arrays.asList(dataPoint2, dataPoint3));

        Cluster<EuclidianDistanceDataPoint> resultingCluster = cluster.merge(cluster2);

        centroid = resultingCluster.getCentroid();
        assertThat(centroid.getDimension(0), Is.is(0.0));
        assertThat(centroid.getDimension(1), Is.is(11.0 + 2.0 / 3.0));
        assertThat(centroid.getDimension(2), Is.is(5.0));
    }

}