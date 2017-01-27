package de.hu.flinkydust.data.point;

/**
 * Klassen, die dieses Interface implementieren können die Distanz
 * zu anderen Klassen, die ebenfalls dieses Interface implementieren,
 * berechnen.
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public interface DistanceMeasurableDataPoint<T extends DistanceMeasurableDataPoint<T>> {

    /**
     * Gibt die Distanz zu einem anderen Punkt zurück
     * @param otherPoint
     *          Der andere Punkt
     * @return
     *          Die Distanz
     */
    double getDistanceTo(T otherPoint);

}
