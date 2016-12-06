package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.projector.FieldnameProjector;
import de.hu.flinkydust.server.rest.datastore.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
@Path("/projection")
public class ProjectionEndpoint extends DataPointRestEndpoint {

    @GET
    @Path("{fields:(/?[^/]+)+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response projectOnStream(@PathParam("fields")List<PathSegment> fieldList) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        dataSource = dataSource
                .selection(dataPoint -> {
                    boolean result = true;
                    for (PathSegment pathSegment : fieldList) {
                        String field = pathSegment.getPath();
                        result &= dataPoint.getOptionalValue(dataPoint.getFieldIndex(field)).isPresent();
                    }
                    return result;
                }).projection(
                new FieldnameProjector(
                        new ArrayList<>(fieldList).stream()
                                .map(PathSegment::getPath)
                                .toArray(String[]::new)));


        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointToJsonGenerator);
    }
}
