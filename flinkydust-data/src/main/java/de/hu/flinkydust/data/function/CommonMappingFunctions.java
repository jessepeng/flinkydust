package de.hu.flinkydust.data.function;

import de.hu.flinkydust.data.datapoint.DustDataPoint;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by Jan-Christopher on 04.02.2017.
 */
public class CommonMappingFunctions {

    public static final Function<DustDataPoint, DustDataPoint> distributionMapper = (dustDataPoint -> {
        double sumOfDimensions = Arrays.stream(dustDataPoint.getAllDimensions()).sum();
        if (sumOfDimensions != 0.0) {
            for (int i = 0; i < dustDataPoint.getArity(); i++) {
                if (i != dustDataPoint.getFieldIndex("MasterTime")) {
                    dustDataPoint.setField(i, dustDataPoint.getDoubleField(i) / sumOfDimensions);
                }
            }
        }
        return dustDataPoint;
    });
}
