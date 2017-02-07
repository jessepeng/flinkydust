package de.hu.flinkydust.data.projector;

import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by andreas on 20.11.16.
 */


public class FieldnameProjector implements Function<DustDataPoint, DustDataPoint> {

    private Map<String, Integer> fieldNameMap;
    private int arity;

    public FieldnameProjector(String...fieldNames) {
        this.fieldNameMap = DustDataPoint.createFieldMap(fieldNames);
        this.arity = fieldNames.length;
    }

    public DustDataPoint apply(DustDataPoint source){
        DustDataPoint result = new DustDataPoint(arity, fieldNameMap);

        for (Map.Entry<String, Integer> fieldEntry : fieldNameMap.entrySet()) {
            result.setField(fieldEntry.getValue(), source.getOptionalValue(fieldEntry.getKey()).orElse(null));
        }

        return result;
    }

}