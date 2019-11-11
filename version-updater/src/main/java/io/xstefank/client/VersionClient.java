package io.xstefank.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("/")
@RegisterRestClient
public interface VersionClient {
    
    @GET
    String getVersion(@HeaderParam("Authorization") String authorization);
}
