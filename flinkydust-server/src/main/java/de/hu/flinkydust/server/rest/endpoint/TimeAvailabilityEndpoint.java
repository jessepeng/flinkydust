package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.server.rest.AbstractResourceResponse;
import de.hu.flinkydust.server.rest.datastore.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jan-Christopher on 06.12.2016.
 */
@Path("/availability")
public class TimeAvailabilityEndpoint extends AbstractResourceResponse {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimeAvailability() {
        DataSource<DustDataPoint> dataSource = DataStore.getInstance().getDataSource(DustDataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        return getResponse(dataSource);
    }

    private Response getResponse(DataSource<DustDataPoint> dataSource) {
        Map<Date, Boolean> dateMap = dataSource
                .selection(DustDataPoint::hasData)
                .stream()
                .collect(Collectors.groupingBy(DustDataPoint::getDate, Collectors.reducing(true, e -> true, (b, c) -> true )));
        return createOkResponse(dateMap.keySet().stream(), (date, jsonGenerator) -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName("date");
                jsonGenerator.writeObject(date);
                jsonGenerator.writeEndObject();
            } catch (IOException e) {

            }
        });
    }

    @GET
    @Path("/filter/{filter:(/?[^/]+/(atLeast|lessThan|same)/[^/]+(/or/[^/]+/(atLeast|lessThan|same)/[^/]+)*)+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filter(@PathParam("filter") List<PathSegment> filterList) {
        DataSource<DustDataPoint> dataSource = DataStore.getInstance().getDataSource(DustDataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }
        dataSource = filterDataSource(filterList, dataSource);
        return getResponse(dataSource);
    }

}
