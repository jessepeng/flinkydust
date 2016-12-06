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

    /**
     * Erzeugt eine einfache Fehler Response mit der angegebenen Nachricht.
     * @param message
     *          Die Nachricht
     * @return
     *          Die Response
     */
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

    /**
     * Erzeugt eine OK Response aus dem angegebenen DataStream, unter Nutzung des angegebenen JsonGeneratorConsumers.
     * @param dataStream
     *          Der Stream
     * @param jsonGeneratorConsumer
     *          Der JsonGeneratorConsumer, der jedes Element des Streams erhält
     * @param <T>
     *          Der Typ des Streams
     * @return
     *          Die Response
     */
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

    /**
     * Schreibt einen DataPoint mittels eines JsonGenerators
     * @param dataPoint
     *          Der DataPoint
     * @param jsonGenerator
     *          Der JsonGenerator
     */
    //TODO: Better error handling
    protected static void writeDataPointToJsonGenerator(DataPoint dataPoint, JsonGenerator jsonGenerator) {
        try {
            jsonGenerator.writeStartObject();
            dataPoint.getFieldIndexMap().entrySet().forEach((fieldIndexEntry) -> {
                Optional<?> optionalValue = dataPoint.getField(fieldIndexEntry.getValue());
                if (optionalValue.isPresent()) {
                    try {
                        jsonGenerator.writeFieldName(fieldIndexEntry.getKey());
                        jsonGenerator.writeObject(optionalValue.get());
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
     * Erzeugt eine OK Response ohne weiteren Inhalt
     * @return
     *          Die Response
     */
    protected static Response createOkResponse() {
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
