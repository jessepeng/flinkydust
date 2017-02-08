package de.hu.flinkydust.data;

/**
 * Ein simples Tupel mit zwei Werten.
 *
 * Created by Jan-Christopher on 30.01.2017.
 */
public class SimpleTuple<T, Y> {
    public T f0;
    public Y f1;

    public SimpleTuple(T f0, Y f1) {
        this.f0 = f0;
        this.f1 = f1;
    }
}
