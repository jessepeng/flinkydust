package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDataPointStreamDataSourceTest {

    @Ignore
    @Test
    public void testClustering() throws Exception {
        EuclidianDataPointDataSource<DataPoint> dataSource = EuclidianDataPointStreamDataSource.readFile("data/dust-2014.dat");
        long timeBefore = System.nanoTime();
        dataSource.hierarchicalCentroidClustering();
        long timeAfter = System.nanoTime();

        System.out.println("Clustering: Elapsed seconds: " + ((timeAfter - timeBefore) / 1000000000.0));

    }

    @Test
    public void testRandomClustering() throws Exception {
        EuclidianDataPointDataSource<DataPoint> dataSource = EuclidianDataPointStreamDataSource.generateRandomData(1000);
        Cluster<DataPoint> cluster = dataSource.hierarchicalCentroidClustering();
    }

}