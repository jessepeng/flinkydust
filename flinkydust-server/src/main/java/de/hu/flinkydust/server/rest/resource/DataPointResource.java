package de.hu.flinkydust.server.rest.resource;

import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.EuclidianDataPointDataSource;
import de.hu.flinkydust.data.EuclidianDataPointStreamDataSource;
import de.hu.flinkydust.data.aggregator.TimeWindowAggregator;
import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.server.rest.AbstractResourceResponse;
import de.hu.flinkydust.server.rest.endpoint.ProjectionEndpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Jan-Christopher on 10.12.2016.
 */
public class DataPointResource extends AbstractResourceResponse {

    private DataSource<DustDataPoint> dataSource;

    public DataPointResource(DataSource<DustDataPoint> dataSource) {
        this.dataSource = dataSource;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response projectAsJsonObject() {
        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsObject);
    }

    @GET
    @Path("/array")
    @Produces(MediaType.APPLICATION_JSON)
    public Response projectAsArray() {
        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsArray);
    }

    @GET
    @Path("/filter/{filter:(/?[^/]+/(atLeast|lessThan|same)/[^/]+(/or/[^/]+/(atLeast|lessThan|same)/[^/]+)*)+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filter(@PathParam("filter") List<PathSegment> filterList) {
        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }
        try {
            dataSource = filterDataSource(filterList, dataSource);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage());
        }
        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsObject);
    }

    @Path("/cluster/window/{hours:\\d+}")
    public ClusterResource cluster(@PathParam("hours") int hours) {
        dataSource = dataSource.aggregation(new TimeWindowAggregator(hours));
        if (dataSource instanceof EuclidianDataPointDataSource) {
            return new ClusterResource(((EuclidianDataPointDataSource<DustDataPoint>)dataSource).hierarchicalCentroidClustering());
        } else {
            return new ClusterResource(new EuclidianDataPointStreamDataSource<>(dataSource.stream()).hierarchicalCentroidClustering());
        }
    }


}
