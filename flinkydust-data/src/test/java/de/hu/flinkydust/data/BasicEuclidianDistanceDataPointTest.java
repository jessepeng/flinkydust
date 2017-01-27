package de.hu.flinkydust.data;

import static org.junit.Assert.*;
import org.hamcrest.core.Is;
import org.junit.Test;

/**
 * Created by Jan-Christopher on 27.01.2017.
 */
public class BasicEuclidianDistanceDataPointTest {

    @Test
    public void testEmptyDataPoint() {
        int dimensions = (int)(Math.random() * 100);
        BasicEuclidianDistanceDataPoint dataPoint = new BasicEuclidianDistanceDataPoint(dimensions);

        assertThat(dataPoint.getDimensionCount(), Is.is(dimensions));
        for (int i = 0; i < dimensions; i++) {
            assertThat(dataPoint.getDimension(i), Is.is(0.0));
        }
    }

    @Test
    public void testDataPointWithData() {
        int dimensions = (int)(Math.random() * 100);
        double[] dimensionArray = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            dimensionArray[i] = Math.random() * 100.0;
        }

        BasicEuclidianDistanceDataPoint dataPoint = new BasicEuclidianDistanceDataPoint(dimensionArray);

        assertThat(dataPoint.getDimensionCount(), Is.is(dimensions));
        for (int i = 0; i < dimensions; i++) {
            assertThat(dataPoint.getDimension(i), Is.is(dimensionArray[i]));
        }
    }

}