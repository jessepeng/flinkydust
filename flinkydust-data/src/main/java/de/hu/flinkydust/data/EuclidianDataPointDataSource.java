package de.hu.flinkydust.data;

import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

import java.util.List;

/**
 * Interface, das die DataSource um die Möglichkeit, Cluster zu bilden, erweitert.
 * Dazu ist in der aktuellen Implementierung erforderlich, dass die Datenpunkte auf
 * einem euklidischen Raum dargestellt werden können.
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public interface EuclidianDataPointDataSource<T extends EuclidianDistanceDataPoint> extends DataSource<T> {

    /**
     * Erzeugt die angegebene Menge an Cluster mithilfe des Centroid-
     * Verfahrens. Dieses Verfahren ist ein hierarchisches Verfahren, bei
     * dem zunächst alle Datenpunkte einen eigenen Cluster bilden. In jedem
     * Vereinigungsschritt werden nun die zwei Cluster zusammengeführt,
     * deren Centroiden am nähesten zueinander sind, bis die gewünschte
     * Anzahl an Cluster erhalten ist.
     * @param noOfClusters
     *          Gewünschte Anzahl an Clustern
     * @return
     *          Die Liste mit den Clustern
     */
    List<Cluster<T>> hierarchicalCentroidClustering(int noOfClusters);
}
