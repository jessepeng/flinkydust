package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceMeasurableDataPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class StructuredEuclidianDistanceCluster extends EuclidianDistanceCluster {

    Cluster<EuclidianDistanceMeasurableDataPoint> cluster1 = null;
    Cluster<EuclidianDistanceMeasurableDataPoint> cluster2 = null;

    public StructuredEuclidianDistanceCluster(EuclidianDistanceMeasurableDataPoint dataPoint) {
        super(dataPoint);
    }

    public StructuredEuclidianDistanceCluster(Cluster<EuclidianDistanceMeasurableDataPoint> cluster1, Cluster<EuclidianDistanceMeasurableDataPoint> cluster2) {
        this.cluster1 = cluster1;
        this.cluster2 = cluster2;
        recalculateCentroid();
    }

    @Override
    public List<EuclidianDistanceMeasurableDataPoint> getPoints() {
        if (cluster1 == null || cluster2 == null) {
            return super.getPoints();
        }
        List<EuclidianDistanceMeasurableDataPoint> pointList = new ArrayList<>(cluster1.getPoints());
        pointList.addAll(cluster2.getPoints());
        return pointList;
    }

    @Override
    public Cluster<EuclidianDistanceMeasurableDataPoint> merge(Cluster<EuclidianDistanceMeasurableDataPoint> otherCluster) {
        return new StructuredEuclidianDistanceCluster(this, otherCluster);
    }
}
