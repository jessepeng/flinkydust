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
public class ProjectionResource extends AbstractResourceResponse {

    private DataSource<DataPoint> dataSource;

    public ProjectionResource(DataSource<DataPoint> dataSource) {
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
    @Path("/filter/{filter:(/?[^/]+/(atLeast|lessThan|same)/[^/]+)+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filter(@PathParam("filter") List<PathSegment> filterList) {
        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }
        for (int i = 0; i < filterList.size(); i += 3) {
            String field = filterList.get(i).getPath();
            String op = filterList.get(i + 1).getPath();
            String value = filterList.get(i + 2).getPath();
            switch (op) {
                case "atLeast":
                    dataSource = dataSource.selection(DataPointComparator.dataPointAtLeastComparator(field, value));
                    break;
                case "lessThan":
                    dataSource = dataSource.selection(DataPointComparator.dataPointLessThanComparator(field, value));
                    break;
                case "same":
                    dataSource = dataSource.selection(DataPointComparator.dataPointSameComparator(field, value));
                    break;
            }    
        }
        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsObject);
    }

}
