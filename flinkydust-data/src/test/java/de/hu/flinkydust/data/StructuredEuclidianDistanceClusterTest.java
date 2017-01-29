package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class StructuredEuclidianDistanceClusterTest {

    @Test
    public void testMerge() {
        EuclidianDistanceDataPoint dataPoint1 = new BasicEuclidianDistanceDataPoint(-1.0, 10.0, 3.0);
        EuclidianDistanceDataPoint dataPoint2 = new BasicEuclidianDistanceDataPoint(0.0, 5.0, 2.0);
        EuclidianDistanceDataPoint dataPoint3 = new BasicEuclidianDistanceDataPoint(1.0, 20.0, 10.0);

        Cluster<EuclidianDistanceDataPoint> cluster1 = new StructuredEuclidianDistanceCluster<>(dataPoint1);
        Cluster<EuclidianDistanceDataPoint> cluster2 = new StructuredEuclidianDistanceCluster<>(dataPoint2);
        Cluster<EuclidianDistanceDataPoint> cluster3 = new StructuredEuclidianDistanceCluster<>(dataPoint3);

        Cluster<EuclidianDistanceDataPoint> resultingCluster = cluster1.merge(cluster2);
        resultingCluster = resultingCluster.merge(cluster3);

        EuclidianDistanceDataPoint centroid = resultingCluster.getCentroid();
        assertThat(centroid.getDimension(0), Is.is(0.0));
        assertThat(centroid.getDimension(1), Is.is(11.0 + 2.0 / 3.0));
        assertThat(centroid.getDimension(2), Is.is(5.0));
    }

}