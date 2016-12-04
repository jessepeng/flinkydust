package de.hu.flinkydust.server.rest.endpoint;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.StreamDataSource;
import de.hu.flinkydust.server.rest.datastore.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
@Path("/data")
public class LoadTestDataEndpoint extends DataPointRestEndpoint {

    @GET
    @Path("/loadTest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadTestData() {
        try {
            DataStore.getInstance().putDataSource(DataPoint.class, StreamDataSource.readFile("data/dust-2014.dat"));
        } catch (IOException e) {
            return createErrorResponse(e.getMessage());
        }

        return Response.ok().entity((StreamingOutput) (stream) -> {
            JsonGenerator jsonGenerator = new ObjectMapper().getFactory().createGenerator(stream);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("status");
            jsonGenerator.writeString("ok");
            jsonGenerator.writeEndObject();

            jsonGenerator.flush();
            jsonGenerator.close();
        }).type(MediaType.APPLICATION_JSON).build();
    }

}
