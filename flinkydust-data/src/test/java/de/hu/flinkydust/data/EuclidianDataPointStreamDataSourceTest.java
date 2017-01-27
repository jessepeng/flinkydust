package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThat;

/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDataPointStreamDataSourceTest {

    @Ignore
    @Test
    public void testClustering() throws Exception {
        EuclidianDataPointDataSource<DataPoint> dataSource = EuclidianDataPointStreamDataSource.readFile("data/dust-2014.dat");
        List<Cluster<DataPoint>> clusterList = dataSource.hierarchicalCentroidClustering(1);
        assertThat(clusterList.size(), Is.is(1));
    }

}