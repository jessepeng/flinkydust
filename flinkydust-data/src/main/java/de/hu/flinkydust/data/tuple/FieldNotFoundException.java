package de.hu.flinkydust.data.tuple;

/**
 * Exception falls ein Feld nicht gefunden wurde.
 *
 * Created by Jan-Christopher on 07.02.2017.
 */
public class FieldNotFoundException extends IllegalArgumentException {
    public FieldNotFoundException() {
    }

    public FieldNotFoundException(String s) {
        super(s);
    }

    public FieldNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldNotFoundException(Throwable cause) {
        super(cause);
    }
}
