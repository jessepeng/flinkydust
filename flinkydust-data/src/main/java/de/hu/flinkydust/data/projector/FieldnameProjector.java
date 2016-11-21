package de.hu.flinkydust.data.projector;
import de.hu.flinkydust.data.StreamDataSource;
import org.apache.flink.api.java.tuple.*;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import org.apache.flink.api.java.tuple.Tuple;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by andreas on 20.11.16.
 */


public class FieldnameProjector {

    private DataSource<DataPoint> source;
    private String[] fieldNames;

    public FieldnameProjector( DataSource<DataPoint> source, String[] fieldNames ){
        this.source = source;
        this.fieldNames = fieldNames;
    }

    public DataSource<DataPoint> project( ){

        return source.projection(value -> {
            DataPoint p = new DataPoint();

            for(String field: fieldNames){
                p.setField(field, value.getOptionalValue(value.getFieldIndex(field)).orElse(null));
            }

            return p;
        });
    }

}