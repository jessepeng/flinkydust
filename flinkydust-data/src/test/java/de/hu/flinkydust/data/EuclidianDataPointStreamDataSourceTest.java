package de.hu.flinkydust.data;

import de.hu.flinkydust.data.aggregator.TimeWindowAggregator;
import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.projector.FieldnameProjector;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThat;

/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDataPointStreamDataSourceTest {

    @Test
    public void testClustering() throws Exception {
        List<DustDataPoint> dataPointList = StreamDataSource.readFile("data/dust-2014.dat")
                .projection(new FieldnameProjector("MasterTime", "Small", "Large"))
                .aggregation(new TimeWindowAggregator(6)).collect();

        clusterAndTestList(dataPointList, "dust-2014.dat");
    }

    @Test
    @Ignore
    public void testClusteringClasses() throws Exception {
        List<DustDataPoint> dataPointList = StreamDataSource.readFile("data/dust-32-grain-size-classes-2014.dat")
                .projection(new FieldnameProjector("MasterTime", "GrainSize0_25", "GrainSize0_28", "GrainSize0_30", "GrainSize0_35", "GrainSize0_40", "GrainSize0_45", "GrainSize0_50", "GrainSize0_58", "GrainSize0_65", "GrainSize0_70", "GrainSize0_80", "GrainSize1_0", "GrainSize1_3", "GrainSize1_6", "GrainSize2_0", "GrainSize2_5", "GrainSize3_0", "GrainSize3_5", "GrainSize4_0", "GrainSize5_0", "GrainSize6_5", "GrainSize7_5", "GrainSize8_0", "GrainSize10_0", "GrainSize12_5", "GrainSize15_0", "GrainSize17_5", "GrainSize20_0", "GrainSize25_0", "GrainSize30_0", "GrainSize32_0"))
                .aggregation(new TimeWindowAggregator(6)).collect();

        clusterAndTestList(dataPointList, "dust-32-grain-size-classes-2014.dat");
    }

    private void clusterAndTestList(List<DustDataPoint> dataPointList, String fileName) {
        EuclidianDataPointStreamDataSource<DustDataPoint> clusterableDataSource = new EuclidianDataPointStreamDataSource<>(dataPointList.stream());

        long timeBefore = System.nanoTime();
        Cluster<DustDataPoint> cluster = clusterableDataSource.hierarchicalCentroidClustering();
        long timeAfter = System.nanoTime();

        System.out.println("Elapsed seconds for clustering " + fileName + ": " + ((timeAfter - timeBefore) / 1000000000.0));

        List<DustDataPoint> clusteredPoints = cluster.getPoints();

        assertThat(dataPointList.size(), Is.is(clusteredPoints.size()));
        clusteredPoints.sort((dustDataPoint, dustDataPoint2) -> (dustDataPoint.getDate().compareTo(dustDataPoint2.getDate())));
        dataPointList.sort((dustDataPoint, dustDataPoint2) -> (dustDataPoint.getDate().compareTo(dustDataPoint2.getDate())));

        assertThat(dataPointList, Is.is(clusteredPoints));
    }

    @Test
    public void testRandomClustering() throws Exception {
        EuclidianDataPointDataSource<DustDataPoint> dataSource = EuclidianDataPointStreamDataSource.generateRandomData(10);
        Cluster<DustDataPoint> cluster = dataSource.hierarchicalCentroidClustering();
    }

}