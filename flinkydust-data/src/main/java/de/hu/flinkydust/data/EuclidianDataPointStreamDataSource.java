package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

import java.io.IOException;
import java.util.Iterator;
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

    public static EuclidianDataPointDataSource<DustDataPoint> readFile(String path) throws IOException {
        return new EuclidianDataPointStreamDataSource<>(parseFile(path));
    }

    public static EuclidianDataPointDataSource<DustDataPoint> generateRandomData(Integer size) {
        List<DustDataPoint> dataPoints = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            dataPoints.add(generateRandomTuple());
        }

        return new EuclidianDataPointStreamDataSource<>(dataPoints.stream());
    }

    /**
     * {@inheritDoc}<br><br>
     * Diese Methode nutzt den von Day und Edelsbrunner (1984) vorgeschlagenen Centroid-Algorithmus,
     * der eine Laufzeit von O(n^2) hat. Damit dieser Algorithmus quadratische Komplexität hat,
     * setzt er voraus, dass jeder Cluster nur durch einen einzigen Punkt repräsentiert ist und
     * dieser beim Zusammenführen durch einen neuen Punkt ersetzt wird.
     */
    @Override
    public Cluster<T> hierarchicalCentroidClustering() {
        // Erzeuge einen Cluster pro ursprünglichem Eintrag
        // O(n), da hinzufügen eines Elements in eine LinkedList O(1)
        final List<Cluster<T>> clusterList = new LinkedList<>();
        stream().forEach(t -> clusterList.add(new StructuredMeanDistanceCluster<>(t)));

        // Initialie Berechnung der nächsten Nachbarcluster jedes einzelnen Clusters
        // O(n^2)
        LinkedList<SimpleTuple<Cluster<T>, Cluster<T>>> nearestNeighborList = new LinkedList<>();
        for (Cluster<T> cluster : clusterList) {
            Cluster<T> nearestNeighbor = null;
            T clusterCentroid = cluster.getCentroid();
            double minDistance = Double.POSITIVE_INFINITY;
            for (Cluster<T> clusterCompare : clusterList) {
                if (!cluster.equals(clusterCompare)) {
                    T clusterCompareCentroid = clusterCompare.getCentroid();
                    double distance = clusterCentroid.getDistanceTo(clusterCompareCentroid);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestNeighbor = clusterCompare;
                        if (minDistance == 0.0) {
                            // Optimierung, um im Falle von identischen Clustern sofort abzubrechen
                            // Bringt keine Optimierung der asymptotischen Laufzeit
                            break;
                        }
                    }
                }
            }
            nearestNeighborList.add(new SimpleTuple<>(cluster, nearestNeighbor));
        }

        // Eigentlicher Algorithmus
        // Äußere Schleife O(n)
        for (int i = clusterList.size(); i >= 2; i--) {
            // Schritt 1
            // Finde das Cluster-Paar mit dem geringsten Abstand
            // O(i)
            double minDistance = Double.POSITIVE_INFINITY;
            SimpleTuple<Cluster<T>, Cluster<T>> closestPair = null;
            for (SimpleTuple<Cluster<T>, Cluster<T>> clusterPair : nearestNeighborList) {
                double pairDistance = clusterPair.f0.getCentroid().getDistanceTo(clusterPair.f1.getCentroid());
                if (pairDistance < minDistance) {
                    minDistance = pairDistance;
                    closestPair = clusterPair;
                    if (minDistance == 0.0) {
                        // Wieder: Optimierung, um im Falle von identischen Clustern sofort abzubrechen.
                        break;
                    }
                }
            }

            assert closestPair != null;

            // Schritt 2
            // Ersetze Cluster i und j aus Clusterpaar mit dem neuen Cluster h
            // O(1), da StructuredMeanDistanceCluster genutzt wird.
            Cluster<T> mergedCluster = closestPair.f0.merge(closestPair.f1);

            // O(1), da LinkedList
            nearestNeighborList.add(new SimpleTuple<>(mergedCluster, null));

            // Schritt 3
            // Liste der nächsten Nachbarn aktualisieren
            // O(a * m), wobei a die Anzahl der Cluster ist, bei denen einer der beiden vorangegangenen Cluster
            // als nächster Nachbar eingetragen war
            for (Iterator<SimpleTuple<Cluster<T>, Cluster<T>>> neighborIterator = nearestNeighborList.iterator(); neighborIterator.hasNext(); ) {
                SimpleTuple<Cluster<T>, Cluster<T>> neighborPair = neighborIterator.next();
                if (neighborPair.f0 == closestPair.f0 || neighborPair.f0 == closestPair.f1) {
                    // Einträge, die auf die zusammengeführten Cluster verweisen, werden nicht mehr benötigt
                    // O(1), da LinkedList
                    neighborIterator.remove();
                    continue;
                }

                if (neighborPair.f1 == closestPair.f0 || neighborPair.f1 == closestPair.f1 || neighborPair.f1 == null) {
                    // Einträge, bei denen einer der beiden zusammengeführten Cluster der nächste Nachbar war,
                    // bzw. der nächste Nachbar noch nicht bestimmt ist (dies ist beim gerade hinzugefügten zusammengeführten Cluster der Fall).
                    // Bei diesen muss der nächste Nachbar neu bestimmt werden
                    double newMinDistance = Double.POSITIVE_INFINITY;
                    Cluster<T> newNearestNeighbor = null;
                    for (SimpleTuple<Cluster<T>, Cluster<T>> clusterPair : nearestNeighborList) {
                        if (clusterPair.f0 != neighborPair.f0 && clusterPair.f0 != closestPair.f0 && clusterPair.f0 != closestPair.f1) {
                            double pairDistance = neighborPair.f0.getCentroid().getDistanceTo(clusterPair.f0.getCentroid());
                            if (pairDistance < newMinDistance) {
                                newMinDistance = pairDistance;
                                newNearestNeighbor = clusterPair.f0;
                                if (minDistance == 0.0) {
                                    // Wieder: Optimierung, um im Falle von identischen Clustern sofort abzubrechen.
                                    break;
                                }
                            }
                        }
                    }

                    neighborPair.f1 = newNearestNeighbor;
                }
            }
        }

        return nearestNeighborList.getFirst().f0;
    }
}
