package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.datapoint.DustDataPoint;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDataPointStreamDataSourceTest {

    @Ignore
    @Test
    public void testClustering() throws Exception {
        EuclidianDataPointDataSource<DustDataPoint> dataSource = EuclidianDataPointStreamDataSource.readFile("data/dust-2014.dat");
        long timeBefore = System.nanoTime();
        dataSource.hierarchicalCentroidClustering();
        long timeAfter = System.nanoTime();

        System.out.println("Clustering: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

    }

    @Test
    public void testRandomClustering() throws Exception {
        EuclidianDataPointDataSource<DustDataPoint> dataSource = EuclidianDataPointStreamDataSource.generateRandomData(1000);
        Cluster<DustDataPoint> cluster = dataSource.hierarchicalCentroidClustering();
    }

}