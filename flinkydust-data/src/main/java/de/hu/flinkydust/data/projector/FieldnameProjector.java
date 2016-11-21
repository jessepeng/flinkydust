package de.hu.flinkydust.data.projector;

import de.hu.flinkydust.data.DataPoint;

import java.util.function.Function;

/**
 * Created by andreas on 20.11.16.
 */


public class FieldnameProjector implements Function<DataPoint, DataPoint> {

    private String[] fieldNames;

    public FieldnameProjector(String... fieldNames ){
        this.fieldNames = fieldNames;
    }

    public DataPoint apply(DataPoint source){
        DataPoint p = new DataPoint();

        for(String field: fieldNames){
            p.setField(field, source.getOptionalValue(source.getFieldIndex(field)).orElse(null));
        }

        return p;
    }

}