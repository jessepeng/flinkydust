package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.projector.FieldnameProjector;
import de.hu.flinkydust.server.rest.AbstractResourceResponse;
import de.hu.flinkydust.server.rest.datastore.DataStore;
import de.hu.flinkydust.server.rest.resource.ProjectionResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
@Path("/projection")
public class ProjectionEndpoint extends AbstractResourceResponse {

    @Path("{fields:(/?(?!array|filter)[^/]+)+}")
    public ProjectionResource getProjection(@PathParam("fields") List<PathSegment> fieldList) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return new ProjectionResource(null);
        }

        return new ProjectionResource(dataSource
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
                                        .toArray(String[]::new))));
    }


}
