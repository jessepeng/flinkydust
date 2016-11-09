package de.hu.flinkydust.data;

import org.apache.flink.api.java.DataSet;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * DataSource, die ein Flink {@link DataSet} als Speicherstruktur verwendet.
 * Created by Jan-Christopher on 09.11.2016.
 */
public class DataSetDataSource<T> implements DataSource<T> {

    private DataSet<T> wrappedDataSet;

    public DataSetDataSource(DataSet<T> dataSource) {
        this.wrappedDataSet = dataSource;
    }

    @Override
    public DataSource<T> selection(Predicate<T> predicate) {
        return new DataSetDataSource<>(wrappedDataSet.filter(predicate::test));
    }

    @Override
    public <R> DataSource<R> projection(Function<T, R> projector) {
        return new DataSetDataSource<>(wrappedDataSet.map(projector::apply));
    }

    @Override
    public DataSource<T> reduce(BiFunction<T, T, T> reducer) {
        return new DataSetDataSource<>(wrappedDataSet.reduce(reducer::apply));
    }

    @Override
    public Collection<T> collect() throws Exception {
        return wrappedDataSet.collect();
    }

    @Override
    public DataSource<T> firstN(int count) {
        return new DataSetDataSource<>(wrappedDataSet.first(count));
    }
}
