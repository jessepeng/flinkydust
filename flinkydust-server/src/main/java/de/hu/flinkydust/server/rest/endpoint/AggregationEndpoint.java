package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.aggregator.AggregatorFunction;
import de.hu.flinkydust.data.aggregator.DataPointAggregator;
import de.hu.flinkydust.server.rest.AbstractResourceResponse;
import de.hu.flinkydust.server.rest.datastore.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Jan-Christopher on 11.12.2016.
 */
@Path("/aggregation")
public class AggregationEndpoint extends AbstractResourceResponse {

    @GET
    @Path("/{method:(max|min|avg)}/{field}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response aggregation(@PathParam("method") String method, @PathParam("field") String field) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        dataSource = getDataPointDataSource(dataSource, method, field);

        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsObject);
    }

    @GET
    @Path("/{method:(max|min|avg)}/{field}/filter/{filter:(/?[^/]+/(atLeast|lessThan|same)/[^/]+)+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterAggregation(@PathParam("method") String method, @PathParam("field") String field, @PathParam("filter") List<PathSegment> filterList) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }
        dataSource = filterDataSource(filterList, dataSource);
        dataSource = getDataPointDataSource(dataSource, method, field);

        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsObject);
    }

    private DataSource<DataPoint> getDataPointDataSource(DataSource<DataPoint> dataSource, String method, String field) {
        AggregatorFunction<DataPoint> aggregatorFunction = null;
        switch (method) {
            case "min":
                aggregatorFunction = DataPointAggregator.dataPointMinAggregator(field);
                break;
            case "max":
                aggregatorFunction = DataPointAggregator.dataPointMaxAggregator(field);
                break;
            case "avg":
                aggregatorFunction = DataPointAggregator.dataPointAvgAggregator(field);
                break;
        }
        dataSource = dataSource.aggregation(aggregatorFunction);
        return dataSource;
    }
}
