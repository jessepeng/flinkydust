package de.hu.flinkydust.server.rest.datastore;

import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.StreamDataSource;

import java.util.HashMap;
import java.util.List;
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

    private Map<Class<?>, List<?>> dataSourceMap = new HashMap<>();

    public <T> void putDataSource(Class<T> dataSourceClass, List<T> dataSource) {
        dataSourceMap.put(dataSourceClass, dataSource);
    }

    @SuppressWarnings("unchecked")
    public <T> DataSource<T> getDataSource(Class<T> dataSourceClass) {
        return new StreamDataSource<>((List<T>) dataSourceMap.get(dataSourceClass));
    }

}
