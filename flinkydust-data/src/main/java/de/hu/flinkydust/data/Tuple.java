package de.hu.flinkydust.data;

/**
 * Created by Jan-Christopher on 30.01.2017.
 */
public class Tuple<T, Y> {
    public T f0;
    public Y f1;

    public Tuple(T f0, Y f1) {
        this.f0 = f0;
        this.f1 = f1;
    }
}
