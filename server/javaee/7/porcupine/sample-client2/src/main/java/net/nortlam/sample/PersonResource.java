package net.nortlam.sample;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.nortlam.porcupine.client.Secure;
import net.nortlam.porcupine.common.Grant;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Path("/person")
@Produces(MediaType.APPLICATION_XML)
public class PersonResource implements Serializable {

    private static final Logger LOG = Logger.getLogger(PersonResource.class.getName());

    public PersonResource() {
    }
    
    @Secure(scope = "EMAIL", grant = Grant.AUTHORIZATION_CODE)
    @GET @Path("/authorization")
    public Person getPersonByAuthorizationCode() {
        return new Person("Mauricio", "Leal");
    }
    
    @Secure(scope="EMAIL", grant=Grant.IMPLICIT)
    @GET @Path("/implicit")
    public Person getPersonByImplicit() {
        return new Person("Nadia", "Ulanova");
    }
    
    @Secure(scope="EMAIL", grant=Grant.RESOURCE_OWNER_PASSWORD_CREDENTIALS, 
            username = "maltron@gmail.com", password = "1234567890")
    @GET @Path("/resourceownerpasswordcredentials")
    public Person getPersonByResourceOwnerClientCredentials() {
        return new Person("John", "Doe");
    }

    @Secure(scope="EMAIL", grant=Grant.CLIENT_CREDENTIALS)
    @GET @Path("/clientcredentials")
    public Person getPersonByClientCredentials() {
        return new Person("Maria", "Luc");
    }
}
