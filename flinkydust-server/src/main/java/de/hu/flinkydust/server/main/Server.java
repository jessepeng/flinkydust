package de.hu.flinkydust.server.main;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Created by Jan-Christopher on 04.12.2016.
 */
public class Server {

    /**
     * Sets up a server instance for flinkydust
     * @param args
     *      optional: port where flinkydust should run
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String baseUrl = (args.length > 0) ? "http://localhost:" + args[0] + "/rest": "http://localhost:80/rest";

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), new FlinkydustServerApplication());
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Server.class.getClassLoader(), "web/"), "/");
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        server.start();

        System.out.println(String.format("\nGrizzly-HTTP-Server gestartet mit der URL: %s\nStoppen des Grizzly-HTTP-Servers mit: Strg+C\n", baseUrl));

        Thread.currentThread().join();
    }
}
