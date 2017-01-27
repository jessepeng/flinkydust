package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class StructuredEuclidianDistanceCluster<T extends EuclidianDistanceDataPoint> extends EuclidianDistanceCluster<T> {

    private Cluster<T> cluster1 = null;
    private Cluster<T> cluster2 = null;

    public StructuredEuclidianDistanceCluster(T dataPoint) {
        super(dataPoint);
    }

    public StructuredEuclidianDistanceCluster(Cluster<T> cluster1, Cluster<T> cluster2) {
        this.cluster1 = cluster1;
        this.cluster2 = cluster2;
        this.centroid = cloner.deepClone(cluster1.getCentroid());
        recalculateCentroid();
    }

    @Override
    public List<T> getPoints() {
        if (cluster1 == null || cluster2 == null) {
            return super.getPoints();
        }
        List<T> pointList = new ArrayList<>(cluster1.getPoints());
        pointList.addAll(cluster2.getPoints());
        return pointList;
    }

    @Override
    public Cluster<T> merge(Cluster<T> otherCluster) {
        return new StructuredEuclidianDistanceCluster<>(this, otherCluster);
    }
}
