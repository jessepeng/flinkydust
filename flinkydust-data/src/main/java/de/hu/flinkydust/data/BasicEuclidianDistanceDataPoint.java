package de.hu.flinkydust.data;

import de.hu.flinkydust.data.point.EuclidianDistanceDataPoint;

import java.util.Arrays;

/**
 * Einfache Basisklasse, die einen Datenpunkt in einem euklidischen Raum darstellt.
 *
 * Created by Jan-Christopher on 27.01.2017.
 */
public class BasicEuclidianDistanceDataPoint implements EuclidianDistanceDataPoint {

    private double[] dimensions;

    public BasicEuclidianDistanceDataPoint(int dimensions) {
        this.dimensions = new double[dimensions];
        Arrays.fill(this.dimensions, 0.0);
    }

    public BasicEuclidianDistanceDataPoint(double... values) {
        this.dimensions = values;
    }

    @Override
    public double[] getAllDimensions() {
        return dimensions;
    }

    @Override
    public double getDimension(int index) {
        return dimensions[index];
    }

    @Override
    public void setDimension(int index, double value) {
        this.dimensions[index] = value;
    }

    @Override
    public int getArity() {
        return dimensions.length;
    }
}
