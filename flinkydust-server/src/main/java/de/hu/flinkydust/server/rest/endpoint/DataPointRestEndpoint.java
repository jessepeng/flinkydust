package de.hu.flinkydust.server.rest.endpoint;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hu.flinkydust.data.DataPoint;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
public abstract class DataPointRestEndpoint {

    protected static Response createErrorResponse(final String message) {
       return Response.ok().entity((StreamingOutput) (stream) -> {
            JsonGenerator jsonGenerator = new ObjectMapper().getFactory().createGenerator(stream);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("status");
            jsonGenerator.writeString("error");
            jsonGenerator.writeFieldName("message");
            jsonGenerator.writeString(message);
            jsonGenerator.writeEndObject();

            jsonGenerator.flush();
            jsonGenerator.close();
        }).type(MediaType.APPLICATION_JSON).build();
    }

    protected static <T> Response createOkResponse(final Stream<T> dataStream, JsonGeneratorConsumer<T> jsonGeneratorConsumer) {
        return Response.ok().entity((StreamingOutput) (stream) -> {
            final JsonGenerator jsonGenerator = new ObjectMapper().getFactory().createGenerator(stream);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("status");
            jsonGenerator.writeString("ok");
            jsonGenerator.writeFieldName("data");
            jsonGenerator.writeStartArray();

            dataStream.forEach(dataPoint -> jsonGeneratorConsumer.consume(dataPoint, jsonGenerator));

            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();

            jsonGenerator.flush();
            jsonGenerator.close();
        }).type(MediaType.APPLICATION_JSON).build();
    }

    //TODO: Better error handling
    protected static void writeDataPointToJsonGenerator(DataPoint dataPoint, JsonGenerator jsonGenerator) {
        try {
            jsonGenerator.writeStartObject();
            dataPoint.getFieldIndexMap().entrySet().forEach((fieldIndexEntry) -> {
                Optional<?> optionalValue = dataPoint.getField(fieldIndexEntry.getValue());
                if (optionalValue.isPresent()) {
                    try {
                        jsonGenerator.writeFieldName(fieldIndexEntry.getKey());
                        jsonGenerator.writeString(optionalValue.get().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            jsonGenerator.writeEndObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface, das einen Consumer zur Verfügung steht, der aus einem Objekt des Typs T
     * ein Json-String generiert
     * @param <T>
     */
    @FunctionalInterface
    public interface JsonGeneratorConsumer<T> {

        void consume(T object, JsonGenerator jsonGenerator);

    }
}
