package de.hu.flinkydust.server.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.hu.flinkydust.data.DataPoint;
import de.hu.flinkydust.data.DataSource;
import de.hu.flinkydust.data.comparator.DataPointComparator;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
public abstract class AbstractResourceResponse {

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
            final JsonGenerator jsonGenerator = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).getFactory().createGenerator(stream);
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
    protected static void writeDataPointAsObject(DataPoint dataPoint, JsonGenerator jsonGenerator) {
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

    protected static void writeDataPointAsArray(DataPoint dataPoint, JsonGenerator jsonGenerator) {
        try {
            jsonGenerator.writeStartArray();
            dataPoint.getFieldIndexMap().entrySet().forEach((fieldIndexEntry) -> {
                Optional<?> optionalValue = dataPoint.getField(fieldIndexEntry.getValue());
                if (optionalValue.isPresent()) {
                    try {
                        jsonGenerator.writeObject(optionalValue.get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            jsonGenerator.writeEndArray();
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

    protected DataSource<DataPoint> filterDataSource(List<PathSegment> filterList, DataSource<DataPoint> dataSource) throws IllegalArgumentException {
        for (int i = 0; i < filterList.size(); i += 3) {
            String field = filterList.get(i).getPath();
            String op = filterList.get(i + 1).getPath();
            String value = filterList.get(i + 2).getPath();
            switch (op) {
                case "atLeast":
                    dataSource = dataSource.selection(DataPointComparator.dataPointAtLeastComparator(field, value));
                    break;
                case "lessThan":
                    dataSource = dataSource.selection(DataPointComparator.dataPointLessThanComparator(field, value));
                    break;
                case "same":
                    dataSource = dataSource.selection(DataPointComparator.dataPointSameComparator(field, value));
                    break;
            }
        }
        return dataSource;
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
