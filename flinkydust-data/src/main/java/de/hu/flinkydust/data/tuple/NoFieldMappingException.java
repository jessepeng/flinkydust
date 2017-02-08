package de.hu.flinkydust.data.tuple;

/**
 * Exception falls für ein Feld kein Mapping gefunden wurde.
 *
 * Created by Jan-Christopher on 07.02.2017.
 */
public class NoFieldMappingException extends IllegalStateException {

    public NoFieldMappingException(String s) {
        super(s);
    }

    public NoFieldMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoFieldMappingException(Throwable cause) {
        super(cause);
    }
}
