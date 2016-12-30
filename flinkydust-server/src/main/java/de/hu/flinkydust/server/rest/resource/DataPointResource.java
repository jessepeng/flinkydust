package de.hu.flinkydust.server.rest.resource;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.comparator.DataPointComparator;
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
import java.util.stream.Collectors;

/**
 * Created by Jan-Christopher on 10.12.2016.
 */
public class DataPointResource extends AbstractResourceResponse {

    private DataSource<DataPoint> dataSource;

    public DataPointResource(DataSource<DataPoint> dataSource) {
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

}
