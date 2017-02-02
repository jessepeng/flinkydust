package de.hu.flinkydust.data.projector;

import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.util.function.Function;

/**
 * Created by andreas on 20.11.16.
 */


public class FieldnameProjector implements Function<DustDataPoint, DustDataPoint> {

    private String[] fieldNames;

    public FieldnameProjector(String... fieldNames ){
        this.fieldNames = fieldNames;
    }

    public DustDataPoint apply(DustDataPoint source){
        DustDataPoint p = new DustDataPoint(fieldNames.length);

        int i = 0;
        for (String field : fieldNames) {
            p.setFieldIndex(field, i++);
            p.setField(field, source.getOptionalValue(source.getFieldIndex(field)).orElse(null));
        }

        return p;
    }

}