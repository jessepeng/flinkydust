package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * Cluster-Implementierung, die baumartig ihre Tochtercluster speichert.
 * Bei einem Merge wird das Centroid nicht anhand aller Punkte neu berechnet,
 * sondern nur anhand des Durchschnitts der Centroids der beiden Merge-Kandidaten.
 * <br><br>
 * Created by Jan-Christopher on 27.01.2017.
 */
public class StructuredMeanDistanceCluster<T extends EuclidianDistanceDataPoint> extends EuclidianDistanceCluster<T> {

    /**
     * Tochtercluster 1
     */
    private Cluster<T> cluster1 = null;
    /**
     * Tochtercluster 2
     */
    private Cluster<T> cluster2 = null;

    public StructuredMeanDistanceCluster(T dataPoint) {
        super(dataPoint);
    }

    /**
     * Erzeugt ein neues Cluster
     * @param cluster1
     *      Tochtercluster 1
     * @param cluster2
     *      Tochtercluster 2
     */
    public StructuredMeanDistanceCluster(Cluster<T> cluster1, Cluster<T> cluster2) {
        this.cluster1 = cluster1;
        this.cluster2 = cluster2;
        centroid = cloner.deepClone(cluster1.getCentroid());
        int dimensionCount = centroid.getDimensionCount();

        for (int i = 0; i < dimensionCount; i++) {
            centroid.setDimension(i, (cluster1.getCentroid().getDimension(i) + cluster2.getCentroid().getDimension(i)) / 2.0);
        }
    }

    public Cluster<T> getLeftChild() {
        return this.cluster1;
    }

    public Cluster<T> getRightChild() {
        return this.cluster2;
    }

    /**
     * Returned alle Punkte im Cluster
     * @return
     *      Die Punkte um Cluster
     */
    @Override
    public List<T> getPoints() {
        if (cluster1 == null || cluster2 == null) {
            return super.getPoints();
        }
        List<T> pointList = new ArrayList<>(cluster1.getPoints());
        pointList.addAll(cluster2.getPoints());
        return pointList;
    }

    /**
     * Gibt den Centroid des Clusters zurück
     * @return
     *      Den Centroid
     */
    @Override
    public T getCentroid() {
        return this.centroid;
    }

    /**
     * Mergt das Cluster mit einem andere Cluster
     * @param otherCluster
     *          Der andere Cluster, mit der dieser Cluster zusammengeführt werden soll
     * @return
     */
    @Override
    public Cluster<T> merge(Cluster<T> otherCluster) {
        return new StructuredMeanDistanceCluster<>(this, otherCluster);
    }
}
