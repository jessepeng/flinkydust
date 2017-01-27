package de.hu.flinkydust.data.cluster;

import java.util.List;

/**
 * Klassen, die dieses Interface implementieren, können als Cluster fungieren
 * und für Clustering-Algorithmen verwendet werden. *
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public interface Cluster<T> {

    /**
     * Gibt das Centroid dieses Clusters zurück.
     * @return
     *          Das Centroid
     */
    T getCentroid();

    /**
     * Gibt die Liste der in diesem Cluster vorhandenen Datenpunkte zurück
     * @return
     *          Die Liste mit den Datenpunkten
     */
    List<T> getPoints();

    /**
     * Führt diesen Cluster mit einem anderen Cluster zusammen.
     * @param otherCluster
     *          Der andere Cluster, mit der dieser Cluster zusammengeführt werden soll
     * @return
     *          Der zusammengeführte neue Cluster
     */
    Cluster<T> merge(Cluster<T> otherCluster);

}
