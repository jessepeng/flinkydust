package de.hu.flinkydust.server.rest.datastore;

import de.hu.flinkydust.data.DataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
public class DataStore {

    private static class DataStoreHolder {
        public static DataStore holder = new DataStore();
    }

    public static DataStore getInstance() {
        return DataStoreHolder.holder;
    }

    private Map<Class<?>, DataSource<?>> dataSourceMap = new HashMap<>();

    public <T> void putDataSource(Class<T> dataSourceClass, DataSource<T> dataSource) {
        dataSourceMap.put(dataSourceClass, dataSource);
    }

    @SuppressWarnings("unchecked")
    public <T> DataSource<T> getDataSource(Class<T> dataSourceClass) {
        return (DataSource<T>) dataSourceMap.get(dataSourceClass);
    }

}
