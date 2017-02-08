package de.hu.flinkydust.data.datapoint;

import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;
import de.hu.flinkydust.data.tuple.Tuple;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Klasse, die einen Datenpunkt repräsentiert
 *
 * Created by Jan-Christopher on 19.11.2016.
 */
public class DustDataPoint extends Tuple implements EuclidianDistanceDataPoint {

    public DustDataPoint() {
        super(new Object[0]);
    }

    public DustDataPoint(int arity) {
        super(arity);
    }

    public DustDataPoint(Object[] values) {
        super(values);
    }

    public DustDataPoint(Object[] values, Map<String, Integer> fieldIndexMap) {
        super(values, fieldIndexMap);
    }

    public DustDataPoint(int arity, Map<String, Integer> fieldIndexMap) {
        super(arity, fieldIndexMap);
    }

    @SuppressWarnings("unchecked")
    public Double getRelHumid() {
        return ((Optional<Double>)getField(getFieldIndex("RelHumidity"))).orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    public Double getTemp() {
        return ((Optional<Double>)getField(getFieldIndex("OutdoorTemp"))).orElse(0.0);
    }

    /**
     * Gibt den Doublewert eines Feldes aus
     * @param pos
     *      Position des ausgegebenen Wertes
     * @return
     *      den Wert des Doubles, 0.0 falls er keinen Wert hat
     */
    @SuppressWarnings("unchecked")
    public Double getDoubleField(int pos) {
        return ((Optional<Double>)getField(pos)).orElse(0.0);
    }

    /**
     * Gibt den Doublewert eines Feldes aus
     * @param fieldName
     *      fieldName des ausgegebenen Wertes
     * @return
     *      den Wert des Doubles, 0.0 falls er keinen Wert hat
     */
    public Double getDoubleField(String fieldName) {
        return getDoubleField(getFieldIndex(fieldName));
    }

    @SuppressWarnings("unchecked")
    public Date getDate() {
        return ((Optional<Date>)getField(getFieldIndex("MasterTime"))).orElseThrow(NullPointerException::new);
    }

    /**
     * Gibt einen optionalen Wert zurück, falls er vorhanden ist.
     * @param fieldIndex
     *      index des Feldes des Wertes
     * @param <T>
     *      Typ des Wertes
     * @return
     *      Der Wert, falls vorhanden
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptionalValue(int fieldIndex) {
        try {
            return (Optional<T>)getField(fieldIndex);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Gibt einen optionalen Wert zurück, falls er vorhanden ist.
     * @param field
     *      fieldName des Wertes
     * @param <T>
     *      Typ des Wertes
     * @return
     *      Der Wert, falls vorhanden
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptionalValue(String field) {
        try {
            return (Optional<T>)getField(field);
        }  catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public <T> void setField(int pos, T value) {
        setField(Optional.ofNullable(value), pos);
    }

    public <T> void setField(String field, T value) {
        setField(Optional.ofNullable(value), getFieldIndex(field));
    }

    /**
     * Prüft ob alle Werte eines DataPoints vorhanden sind.
     * @return
     *      true falls alle vorhanden sind, false else
     */
    public boolean hasData() {
        return getOptionalValue(1).isPresent()
                || getOptionalValue(2).isPresent()
                || getOptionalValue(3).isPresent()
                || getOptionalValue(4).isPresent();
    }

    /**
     * Gibt alle Dimensionen zurück
     * @return
     *      Die Dimensionen des Datenpunkts
     */
    @Override
    @SuppressWarnings("unchecked")
    public double[] getAllDimensions() {
        double[] dimensions = new double[2];
        for (int i = 1; i < 3; i++) {
            dimensions[i - 1] = ((Optional<Double>)getField(i)).orElse(0.0);
        }
        return dimensions;
    }

    /**
     * Gibt die Dimensionen am Index zurück
     * @param index
     *      index der Dimension
     * @return
     *      Die Dimensionen des Datenpunkts an Position index
     */
    @Override
    @SuppressWarnings("unchecked")
    public double getDimension(int index) {
        return ((Optional<Double>)getField(index + 1)).orElse(0.0);
    }

    /**
     * Setzt die Dimension am Index
     * @param index
     *          Der Index, dessen Dimension festgelegt werden soll.
     * @param value
     *      Neuer Wert der Dimension
     */
    @Override
    public void setDimension(int index, double value) {
        setField(Optional.ofNullable(value), index + 1);
    }

    /**
     * Gibt die Anzahl der Dimensionen zurück
     * @return
     *      Anzahl der Dimensionen
     */
    @Override
    public int getDimensionCount() {
        return 2;
    }
}
