package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.server.rest.datastore.DataStore;

import javax.print.attribute.standard.Media;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jan-Christopher on 06.12.2016.
 */
@Path("/availability")
public class TimeAvailabilityEndpoint extends DataPointRestEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimeAvailability() {
        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);

        if (dataSource == null) {
            return createErrorResponse("Keine DataSource geladen.");
        }

        Map<Date, Long> dateMap = dataSource
                .selection(DataPoint::hasData)
                .stream()
                .collect(Collectors.groupingBy(DataPoint::getDate, Collectors.counting()));
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

}
