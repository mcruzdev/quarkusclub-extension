package dev.matheuscruz.quarkusclub.extension.runtime;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * PingResource
 */
@Path("/ping")
public class PingResource {

    @GET
    public String ping() {
        return "pong";
    }

}