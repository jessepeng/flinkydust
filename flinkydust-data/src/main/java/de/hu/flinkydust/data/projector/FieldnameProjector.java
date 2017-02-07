package de.hu.flinkydust.data.projector;

import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by andreas on 20.11.16.
 */


public class FieldnameProjector implements Function<DustDataPoint, DustDataPoint> {

    private Set<String> fieldNames;

    public FieldnameProjector(String... fieldNames ){
        this.fieldNames = new HashSet<>(Arrays.asList(fieldNames));
    }

    public DustDataPoint apply(DustDataPoint source){
//        DustDataPoint p = new DustDataPoint(fieldNames.length);
//
//        int i = 0;
//        for (String field : fieldNames) {
//            p.setFieldIndex(field, i);
//            p.setField(i++, source.getOptionalValue(source.getFieldIndex(field)).orElse(null));
//        }
//
//        return p;
        for (String oldField : source.getFieldIndexMap().keySet()) {
            if (!fieldNames.contains(oldField)) {
                source.setField(oldField, null);
            }
        }
        return source;
    }

}