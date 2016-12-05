package de.hu.flinkydust.server.rest.endpoint;

import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.projector.FieldnameProjector;
import de.hu.flinkydust.server.rest.datastore.DataStore;
import de.hu.flinkydust.server.rest.interceptor.Compressed;
import javassist.bytecode.ByteArray;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

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

        dataSource = dataSource.projection(
                new FieldnameProjector(
                        new ArrayList<>(fieldList).stream()
                                .map(PathSegment::getPath)
                                .toArray(String[]::new)));


        return createOkResponse(dataSource.stream());
    }
//
//    @GET
//    @Path("/string{fields:(/?[^/]+)+}")
//    @Produces(MediaType.APPLICATION_JSON)
//    //@Compressed
//    public String projectOnString(@PathParam("fields")List<PathSegment> fieldList) throws Exception {
//        DataSource<DataPoint> dataSource = DataStore.getInstance().getDataSource(DataPoint.class);
//
//        dataSource = dataSource.projection(
//                new FieldnameProjector(
//                        new ArrayList<>(fieldList).stream()
//                                .map(PathSegment::getPath)
//                                .toArray(String[]::new)));
//
//        StringBuilder stringBuilder = new StringBuilder();
//        dataSource.stream().map(ProjectionEndpoint::writeDataPointAsJson).forEach(stringBuilder::append);
//        return stringBuilder.toString();
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos);
////        dataSource.stream().map(ProjectionEndpoint::writeDataPointAsJson).forEach((string) -> {
////            try {
////                gzipOutputStream.write(string.getBytes());
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        });
////        gzipOutputStream.flush();
////        return bos.toString();
//    }

}
