package de.hu.flinkydust.data;

/**
 * Created by robin on 20.11.16.
 */
public class Profiler {
    public void main(String[] args) {
        int size = Integer.parseInt(args[0]);

        long startGeneratingTime = System.nanoTime();
        DataSource<DataPoint> dataSource = StreamDataSource.generateRandomData(size);

        long startProjectionTime = System.nanoTime();

        System.out.print("The time to generate " + args[0] + " random DataPoint objects: " + String.valueOf(startProjectionTime - startGeneratingTime));
    }
}
