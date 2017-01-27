package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceMeasurableDataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Cluster, der Datenpunkte in einem euklidischen Raum speichern kann.
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDistanceCluster implements Cluster<EuclidianDistanceMeasurableDataPoint> {

    private EuclidianDistanceMeasurableDataPoint centroid;

    private List<EuclidianDistanceMeasurableDataPoint> dataPoints;

    public EuclidianDistanceCluster(EuclidianDistanceMeasurableDataPoint dataPoint) {
        dataPoints = new ArrayList<>();
        dataPoints.add(dataPoint);
        recalculateCentroid();
    }

    public EuclidianDistanceCluster(List<EuclidianDistanceMeasurableDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        recalculateCentroid();
    }

    EuclidianDistanceCluster() {

    }

    @Override
    public EuclidianDistanceMeasurableDataPoint getCentroid() {
        return centroid;
    }

    @Override
    public List<EuclidianDistanceMeasurableDataPoint> getPoints() {
        return dataPoints;
    }

    @Override
    public Cluster<EuclidianDistanceMeasurableDataPoint> merge(Cluster<EuclidianDistanceMeasurableDataPoint> otherCluster) {
        EuclidianDistanceCluster newCluster = new EuclidianDistanceCluster(getPoints());
        newCluster.getPoints().addAll(otherCluster.getPoints());
        newCluster.recalculateCentroid();
        return newCluster;
    }

    /**
     * Berechnet den Centroid dieses Clusters neu.
     */
    protected void recalculateCentroid() {
        int dimensionCount;
        if (getPoints().isEmpty()) {
            return;
        }
        if (centroid != null) {
            dimensionCount = centroid.getDimensionCount();
        } else {
            dimensionCount = getPoints().get(0).getDimensionCount();
        }
        int noOfDataPoints = getPoints().size();

        BasicEuclidianDistanceDataPoint newCentroid = new BasicEuclidianDistanceDataPoint(dimensionCount);

        getPoints().forEach(dataPoint -> {
            for (int i = 0; i < dimensionCount; i++) {
                newCentroid.setDimension(i, newCentroid.getDimension(i) + dataPoint.getDimension(i));
            }
        });
        for (int i = 0; i < dimensionCount; i++) {
            newCentroid.setDimension(i, newCentroid.getDimension(i) / noOfDataPoints);
        }
        this.centroid = newCentroid;
    }
}
