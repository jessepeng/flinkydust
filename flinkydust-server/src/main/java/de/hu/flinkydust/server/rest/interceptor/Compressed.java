package de.hu.flinkydust.server.rest.interceptor;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jan-Christopher on 05.12.2016.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Compressed {
}
