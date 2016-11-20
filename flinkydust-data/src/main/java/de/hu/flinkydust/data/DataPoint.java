package de.hu.flinkydust.data;

import org.apache.flink.api.java.tuple.Tuple5;

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

    public int getFieldIndex(String fieldName) {
        return fieldIndexMap.get(fieldName);
    }

    public Double getField(Integer field) {
        switch (field) {
            case 1:
                return getSmall();
            case 2:
                return (Double) getLarge();
            case 3:
                return (Double) getRelHumid();
            case 4:
                return (Double) getTemp();
        }

        return 0.0;
    }

    public <T> Optional<T> getOptionalValue(int fieldIndex) {
        return getField(fieldIndex);
    }
}
