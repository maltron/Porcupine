package net.nortlam.porcupine.sample.client;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Path("/person")
public class Resource implements Serializable {
    
    private static final Logger LOG = Logger.getLogger(Resource.class.getName());
    
    public static final String OAUTH2_AUTHORIZE_SERVER = "http://localhost:8080";
    public static final String OAUTH2_AUTHORIZE_PATH = "server/oauth2/authorize";
    // Section 4.1.1 Obtaining an Authorization Request through Authorization Code
    public static final String OAUTH2_RESPONSE_TYPE = "code";
    public static final String OAUTH2_CLIENT_ID = "cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=";
    public static final String OAUTH2_SCOPE = "EMAIL";
    public static final String OAUTH2_REDIRECT_URI = "http%3A%2F%2Flocalhost%3A8080%2Fclient";

    public Resource() {
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Person getPerson() {
        return new Person("Mauricio", "Leal");
    }
    
    @GET
    @Path("/test")
    public Response test() throws URISyntaxException {
        return Response.seeOther(new URI("http://localhost:8080/server/oauth2/authorize?response_type=code&client_id=cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=&scope=EMAIL&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fclient")).build();
    }

}
