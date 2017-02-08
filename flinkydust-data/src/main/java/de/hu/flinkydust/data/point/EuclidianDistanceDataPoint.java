package de.hu.flinkydust.data.point;

/**
 * Klassen, die dieses Interface implementieren, können die euklidische Distanz
 * zu einem anderen Punkt berechnen.
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public interface EuclidianDistanceDataPoint extends DistanceDataPoint<EuclidianDistanceDataPoint> {

    /**
     * Gibt die Werte aller Dimensionen dieses Datenpunktes zurück.
     * @return
     *          Die Werte aller Dimensionen
     */
    double[] getAllDimensions();

    /**
     * Gibt den Wert der Dimension an einem bestimmten Index zurück.
     * @param index
     *          Der Index, dessen Dimension ausgegeben werden soll.
     * @return
     *          Der Wert der Dimension an diesem Index
     */
    double getDimension(int index);

    /**
     * Legt den Wert der Dimension an einem bestimmten Index fest.
     * @param index
     *          Der Index, dessen Dimension festgelegt werden soll.
     * @param value
     *          Der Wert, die diese Dimension annehmen soll.
     */
    void setDimension(int index, double value);

    /**
     * Gibt die Anzahl der Dimensionen zurück
     * @return
     *          Die Anzahl der Dimensionen
     */
    int getDimensionCount();

    default void initializeWithZero() {
        for (int i = 0; i < getDimensionCount(); i++) {
            setDimension(i, 0.0);
        }
    }

    /**
     * Gibt die Distanz zwischen zwei Datapoints an.
     * @param dataPoint
     *      Der Datapoint dessen Abstand zu diesem Datapoint gemessen werden soll
     * @return
     *      Die Distanz
     */
    default double getDistanceTo(EuclidianDistanceDataPoint dataPoint) {
        double[] thisDimensions = getAllDimensions();
        double[] otherDimensions = dataPoint.getAllDimensions();
        if (thisDimensions.length != otherDimensions.length) {
            throw new IllegalStateException("Der Abstand zweier Punkte kann nur bei gleicher Anzahl der Dimensionen berechnet werden.");
        }
        double sumOfSquaredDistances = 0.0;
        for (int i = 0; i < thisDimensions.length; i++) {
            sumOfSquaredDistances += Math.pow(thisDimensions[i] - otherDimensions[i], 2.0);
        }
        return Math.sqrt(sumOfSquaredDistances);
    }

}
