package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.projector.FieldnameProjector;
import de.hu.flinkydust.server.rest.AbstractResourceResponse;
import de.hu.flinkydust.server.rest.datastore.DataStore;
import de.hu.flinkydust.server.rest.resource.DataPointResource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
@Path("/projection")
public class ProjectionEndpoint extends AbstractResourceResponse {

    @Path("{fields:(/?(?!array|filter)[^/]+)+}")
    public DataPointResource getProjection(@PathParam("fields") List<PathSegment> fieldList) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return new DataPointResource(null);
        }

        return new DataPointResource(dataSource
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
