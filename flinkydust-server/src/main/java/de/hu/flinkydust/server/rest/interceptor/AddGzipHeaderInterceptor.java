package de.hu.flinkydust.server.rest.interceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

/**
 * Created by Jan-Christopher on 05.12.2016.
 */
@Provider
@Compressed
public class AddGzipHeaderInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        writerInterceptorContext.getHeaders().add(HttpHeaders.CONTENT_ENCODING, "gzip");
        writerInterceptorContext.proceed();
    }
}
