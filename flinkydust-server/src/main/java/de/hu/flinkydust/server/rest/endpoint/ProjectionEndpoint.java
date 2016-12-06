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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public Response projectAsJsonObject(@PathParam("fields") List<PathSegment> fieldList) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        dataSource = getProjection(fieldList, dataSource);


        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsObject);
    }

    private DataSource<DataPoint> getProjection(List<PathSegment> fieldList, DataSource<DataPoint> dataSource) {
        return dataSource
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
    }

    @GET
    @Path("{fields:(/?[^/]+)+}/array")
    @Produces(MediaType.APPLICATION_JSON)
    public Response projectAsArray(@PathParam("fields") List<PathSegment> fieldList) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        dataSource = getProjection(fieldList, dataSource);


        return createOkResponse(dataSource.stream(), ProjectionEndpoint::writeDataPointAsArray);
    }

    @GET
    @Path("{fields:(/?[^/]+)+}/xyobject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response projectAsXYObject(@PathParam("fields") List<PathSegment> fieldList) {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        dataSource = getProjection(fieldList, dataSource);
        final String x = fieldList.get(0).getPath();
        final String y = fieldList.get(1).getPath();
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

        return createOkResponse(dataSource.stream(), (dataPoint, jsonGenerator) -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName("x");
                jsonGenerator.writeObject(dataPoint.getOptionalValue(dataPoint.getFieldIndex(x)).orElse(""));
                jsonGenerator.writeFieldName("y");
                jsonGenerator.writeObject(dataPoint.getOptionalValue(dataPoint.getFieldIndex(y)).orElse(""));
                jsonGenerator.writeFieldName("toolTipContent");
                jsonGenerator.writeString(x + ": {x}, " + y + ": {y}, " + dateFormat.format(dataPoint.getDate()));
                jsonGenerator.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
