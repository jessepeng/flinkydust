package de.hu.flinkydust.data;

import com.rits.cloning.Cloner;
import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Cluster, der Datenpunkte in einem euklidischen Raum speichern kann.
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDistanceCluster<T extends EuclidianDistanceDataPoint> implements Cluster<T> {

    /**
     * Centroid des Clusters
     */
    protected T centroid;

    /**
     * Datenpunkte im Cluster
     */
    private List<T> dataPoints;

    protected static Cloner cloner = new Cloner();

    public EuclidianDistanceCluster(T dataPoint) {
        this.dataPoints = new ArrayList<>();
        this.dataPoints.add(dataPoint);
        this.centroid = cloner.deepClone(dataPoint);
    }

    public EuclidianDistanceCluster(List<T> dataPoints) {
        if (dataPoints.isEmpty()) {
            throw new IllegalArgumentException("Liste der Datenpunkte darf nicht leer sein.");
        }
        this.dataPoints = dataPoints;
        this.centroid = cloner.deepClone(dataPoints.get(0));
    }

    EuclidianDistanceCluster() {

    }

    /**
     * gibt den neu berechneten Centroid zurück
     * @return
     *      den Centroid
     */
    @Override
    public T getCentroid() {
        recalculateCentroid();
        return centroid;
    }

    @Override
    public List<T> getPoints() {
        return dataPoints;
    }

    /**
     * Vereinigt zwei Cluster
     * @param otherCluster
     *          Der andere Cluster, mit der dieser Cluster zusammengeführt werden soll
     * @return
     *      Das neue Cluster
     */
    @Override
    public Cluster<T> merge(Cluster<T> otherCluster) {
        EuclidianDistanceCluster<T> newCluster = new EuclidianDistanceCluster<>(getPoints());
        newCluster.getPoints().addAll(otherCluster.getPoints());
        newCluster.recalculateCentroid();
        return newCluster;
    }

    /**
     * Berechnet den Centroid dieses Clusters neu.
     */
    protected void recalculateCentroid() {
        List<T> pointList = getPoints();
        if (pointList.isEmpty()) {
            return;
        }

        int dimensionCount = centroid.getDimensionCount();
        int noOfDataPoints = pointList.size();

        centroid.initializeWithZero();
        getPoints().forEach(dataPoint -> {
            for (int i = 0; i < dimensionCount; i++) {
                centroid.setDimension(i, centroid.getDimension(i) + dataPoint.getDimension(i));
            }
        });
        for (int i = 0; i < dimensionCount; i++) {
            centroid.setDimension(i, centroid.getDimension(i) / noOfDataPoints);
        }
    }
}
