package de.hu.flinkydust.server.rest.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.hu.flinkydust.data.StructuredMeanDistanceCluster;
import de.hu.flinkydust.data.cluster.Cluster;
import de.hu.flinkydust.data.datapoint.DustDataPoint;
import de.hu.flinkydust.data.function.CommonMappingFunctions;
import de.hu.flinkydust.server.rest.AbstractResourceResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

/**
 * Created by Jan-Christopher on 02.02.2017.
 */
public class ClusterResource extends AbstractResourceResponse {

    private Cluster<DustDataPoint> cluster;

    public ClusterResource(Cluster<DustDataPoint> cluster) {
        this.cluster = cluster;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response projectAsJsonObject() {
        if (cluster == null) {
            return createErrorResponse("Die gewählte DataSource unterstützt kein Clustering.");
        }

        return Response.ok().entity((StreamingOutput) (stream) -> {
            final JsonGenerator jsonGenerator = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).getFactory().createGenerator(stream);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("status");
            jsonGenerator.writeString("ok");
            jsonGenerator.writeFieldName("data");
            jsonGenerator.writeStartArray();

            writeClusterAsJsonObject(jsonGenerator, cluster);

            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();

            jsonGenerator.flush();
            jsonGenerator.close();
        }).type(MediaType.APPLICATION_JSON).build();
    }

    private void writeClusterAsJsonObject(final JsonGenerator jsonGenerator, Cluster<DustDataPoint> dustDataPointCluster) throws IOException {
        jsonGenerator.writeStartObject();
        if (dustDataPointCluster instanceof StructuredMeanDistanceCluster) {
            StructuredMeanDistanceCluster<DustDataPoint> structuredCluster = (StructuredMeanDistanceCluster<DustDataPoint>)dustDataPointCluster;
            jsonGenerator.writeFieldName("centroid");
            writeDataPointAsObject(CommonMappingFunctions.distributionMapper.apply(structuredCluster.getCentroid()), jsonGenerator);

            if (structuredCluster.getLeftChild() != null && structuredCluster.getRightChild() != null) {
                jsonGenerator.writeFieldName("left");
                writeClusterAsJsonObject(jsonGenerator, structuredCluster.getLeftChild());
                jsonGenerator.writeFieldName("right");
                writeClusterAsJsonObject(jsonGenerator, structuredCluster.getRightChild());
            } else {
                jsonGenerator.writeFieldName("point");
                writeDataPointAsObject(CommonMappingFunctions.distributionMapper.apply(structuredCluster.getPoints().get(0)), jsonGenerator);
            }
        }
        jsonGenerator.writeEndObject();
    }

}
