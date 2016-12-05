package de.hu.flinkydust.server.main;

import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;

/**
 * Created by Jan-Christopher on 05.12.2016.
 */
public class FlinkydustServerApplication extends ResourceConfig {

    public FlinkydustServerApplication() {
        packages("de.hu.flinkydust.server.rest");
        EncodingFilter.enableFor(this, GZipEncoder.class);
    }

}
