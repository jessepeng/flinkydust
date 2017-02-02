package de.hu.flinkydust.data.datapoint;

import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;
import de.hu.flinkydust.data.tuple.Tuple;

import java.util.Date;
import java.util.Optional;

/**
 * Klasse, die einen Datenpunkt repräsentiert
 *
 * Created by Jan-Christopher on 19.11.2016.
 */
public class DustDataPoint extends Tuple implements EuclidianDistanceDataPoint {

    public DustDataPoint(int arity) {
        super(arity);
    }

    public DustDataPoint(Object... values) {
        super(convertValuesToOptionals(values));
    }

    public DustDataPoint(String[] fieldNames, Object[] values) {
        super(fieldNames, convertValuesToOptionals(values));
    }

    private static Object[] convertValuesToOptionals(Object[] values) {
        for (int i = 0; i < values.length; i++) {
            if (!(values[i] instanceof Optional<?>)) {
                values[i] = Optional.ofNullable(values[i]);
            }
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public Double getRelHumid() {
        return ((Optional<Double>)getField(getFieldIndex("RelHumidity"))).orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    public Double getTemp() {
        return ((Optional<Double>)getField(getFieldIndex("OutdoorTemp"))).orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    public Double getDoubleField(int pos) {
        return ((Optional<Double>)getField(pos)).orElse(0.0);
    }

    public Double getDoubleField(String fieldName) {
        return getDoubleField(getFieldIndex(fieldName));
    }

    @SuppressWarnings("unchecked")
    public Date getDate() {
        return ((Optional<Date>)getField(getFieldIndex("MasterTime"))).orElseThrow(NullPointerException::new);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptionalValue(int fieldIndex) {
        try {
            return (Optional<T>)getField(fieldIndex);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public <T> void setField(int pos, T value) {
        setField(Optional.ofNullable(value), pos);
    }

    public <T> void setField(String field, T value) {
        setField(Optional.ofNullable(value), getFieldIndex(field));
    }

    public boolean hasData() {
        return getOptionalValue(1).isPresent()
                || getOptionalValue(2).isPresent()
                || getOptionalValue(3).isPresent()
                || getOptionalValue(4).isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public double[] getAllDimensions() {
        double[] dimensions = new double[2];
        for (int i = 1; i < 3; i++) {
            dimensions[i - 1] = ((Optional<Double>)getField(i)).orElse(0.0);
        }
        return dimensions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public double getDimension(int index) {
        return ((Optional<Double>)getField(index + 1)).orElse(0.0);
    }

    @Override
    public void setDimension(int index, double value) {
        setField(Optional.ofNullable(value), index + 1);
    }

    @Override
    public int getDimensionCount() {
        return 2;
    }
}
