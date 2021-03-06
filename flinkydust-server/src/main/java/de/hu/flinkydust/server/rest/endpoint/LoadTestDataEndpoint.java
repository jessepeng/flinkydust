package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.EuclidianDataPointStreamDataSource;
import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.StreamDataSource;
import de.hu.flinkydust.server.rest.AbstractResourceResponse;
import de.hu.flinkydust.server.rest.datastore.DataStore;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
@Path("/data")
public class LoadTestDataEndpoint extends AbstractResourceResponse {

    /**
     * Lädt die Testdaten ein.
     * @return
     *         Reponse-Objekt
     */
    @GET
    @Path("/loadTest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadTestData() {
        try {
            DataStore.getInstance().putDataSource(DustDataPoint.class, EuclidianDataPointStreamDataSource.parseFile(getClass().getClassLoader().getResourceAsStream("data/dust-2014-v2.dat")));
        } catch (IOException e) {
            return createErrorResponse(e.getMessage());
        }

        return createOkResponse();
    }

    /**
     * Lädt die Testdaten ein.
     * @return
     *         Reponse-Objekt
     */
    @GET
    @Path("/loadTestClasses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadTestDataClasses() {
        try {
            DataStore.getInstance().putDataSource(DustDataPoint.class, EuclidianDataPointStreamDataSource.parseFile(getClass().getClassLoader().getResourceAsStream("data/dust-32-grain-size-classes-2014.dat")));
        } catch (IOException e) {
            return createErrorResponse(e.getMessage());
        }

        return createOkResponse();
    }

    /**
     * Lädt eine Datei aus einem InputStream ein
     * @param fileInputStream
     *          InputStream
     * @param fileMetaData
     *          MetaData
     * @return
     *          Response
     */
    @POST
    @Path("/load/file")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file")FormDataContentDisposition fileMetaData) {
        try {
            DataStore.getInstance().putDataSource(DustDataPoint.class, StreamDataSource.parseFile(fileInputStream));
        } catch (IOException e) {
            return createErrorResponse(e.getMessage());
        }

        return createOkResponse();
    }

}
