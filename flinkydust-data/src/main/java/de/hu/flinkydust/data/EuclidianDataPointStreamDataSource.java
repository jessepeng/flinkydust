package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * StreamDataSource, die Clustering ermögicht.
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public class EuclidianDataPointStreamDataSource<T extends EuclidianDistanceDataPoint> extends StreamDataSource<T> implements EuclidianDataPointDataSource<T> {

    public EuclidianDataPointStreamDataSource(List<T> list) {
        super(list);
    }

    public EuclidianDataPointStreamDataSource(Stream<T> dataSource) {
        super(dataSource);
    }

    public static EuclidianDataPointDataSource<DataPoint> readFile(String path) throws IOException {
        return new EuclidianDataPointStreamDataSource<>(parseFile(path));
    }

    public static EuclidianDataPointDataSource<DataPoint> generateRandomData(Integer size) {
        List<DataPoint> dataPoints = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            dataPoints.add(generateRandomTuple());
        }

        return new EuclidianDataPointStreamDataSource<>(dataPoints.stream());
    }

    @Override
    public List<Cluster<T>> hierarchicalCentroidClustering(int noOfClusters) {
        if (noOfClusters < 1) {
            throw new IllegalArgumentException("Anzahl der Cluster muss positiv sein.");
        }
        // Erzeuge einen Cluster pro ursprünglichem Eintrag
        List<Cluster<T>> clusterList = stream().collect(ArrayList::new, (clusters, t) -> clusters.add(new StructuredEuclidianDistanceCluster<T>(t)), ArrayList::addAll);

        while (clusterList.size() > noOfClusters) {
            clusterList = mergeNextCluster(clusterList);
        }

        return clusterList;
    }

    private List<Cluster<T>> mergeNextCluster(List<Cluster<T>> clusterList) {
        Cluster<T> mergeCandidate1 = null;
        Cluster<T> mergeCandidate2 = null;
        OUTER:
        for (Cluster<T> cluster : clusterList) {
            T clusterCentroid = cluster.getCentroid();
            double minDistance = Double.POSITIVE_INFINITY;
            for (Cluster<T> clusterCompare : clusterList) {
                T clusterCompareCentroid = clusterCompare.getCentroid();
                if (!clusterCompare.equals(cluster)) {
                    double distance = clusterCentroid.getDistanceTo(clusterCompareCentroid);
                    if (distance < minDistance) {
                        minDistance = distance;
                        mergeCandidate1 = cluster;
                        mergeCandidate2 = clusterCompare;
                        if (minDistance == 0.0) {
                            // So werden gleiche Kandidaten schneller gemerged
                            break OUTER;
                        }
                    }
                }
            }
        }
        if (mergeCandidate1 != null) {
            Cluster<T> mergedCluster = mergeCandidate1.merge(mergeCandidate2);
            clusterList.remove(mergeCandidate1);
            clusterList.remove(mergeCandidate2);
            clusterList.add(mergedCluster);
        }
        return clusterList;
    }
}
