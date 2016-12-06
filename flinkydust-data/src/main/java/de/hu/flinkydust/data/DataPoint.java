package de.hu.flinkydust.data;

import de.hu.flinkydust.data.tuple.Tuple5;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Klasse, die einen Datenpunkt repräsentiert
 *
 * Created by Jan-Christopher on 19.11.2016.
 */
public class DataPoint extends Tuple5<Optional<Date>, Optional<Double>, Optional<Double>, Optional<Double>, Optional<Double>> {

    private static Map<String, Integer> fieldIndexMap = new HashMap<>();

    public DataPoint() {
        super(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public DataPoint(Date date, Double small, Double large, Double relHumidity, Double temp) {
        super(Optional.ofNullable(date), Optional.ofNullable(small), Optional.ofNullable(large), Optional.ofNullable(relHumidity), Optional.ofNullable(temp));
    }

    static {
        fieldIndexMap.put("date", 0);
        fieldIndexMap.put("small", 1);
        fieldIndexMap.put("large", 2);
        fieldIndexMap.put("relHumid", 3);
        fieldIndexMap.put("temp", 4);
    }

    @SuppressWarnings("unchecked")
    public Double getRelHumid() {
        return ((Optional<Double>)getField(3)).orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    public Double getTemp() {
        return ((Optional<Double>)getField(4)).orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    public Double getLarge() {
        return ((Optional<Double>)getField(2)).orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    public Double getSmall() {
        return ((Optional<Double>)getField(1)).orElse(0.0);
    }

    @SuppressWarnings("unchecked")
    public Date getDate() {
        return ((Optional<Date>)getField(0)).orElseThrow(NullPointerException::new);
    }

    public Integer getFieldIndex(String fieldName) {
        return fieldIndexMap.get(fieldName);
    }

    public Map<String, Integer> getFieldIndexMap() {
        return fieldIndexMap;
    }

    public <T> Optional<T> getOptionalValue(int fieldIndex) {
        return getField(fieldIndex);
    }

    public <T> void setField(String field, T value) {
        setField(Optional.ofNullable(value), getFieldIndex(field));
    }
}
