package net.nortlam.porcupine.websecurity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/person")
public class Person {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Mauricio Leal";
    }

}
