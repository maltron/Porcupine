package net.nortlam.test.authorization;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Test;

import net.nortlam.porcupine.common.authenticator.BasicAuthenticator;

/**
 *
 * @author Mauricio "Maltron" Leal
 */
public class TestBasicAuthorization implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestBasicAuthorization.class.getName());
    
    
    @Test
    public void testingFormAuthenticator() {
    }

    @Test
    public void testingBasicAuthenticator() {
        
//        Client client = ClientBuilder.newClient().register(
//                                    new BasicAuthenticator("maltron@gmail.com", "maltron"));
//        
//        WebTarget target = client.target("http://localhost:8080/testbasic/secured/hello.html");
//
//        LOG.log(Level.INFO, ">>> testing() http://localhost:8080/testbasic/secured/hello.html");
//        Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).get();
//        try {
//
//            LOG.log(Level.INFO, ">>> testing() {0} {1}", new Object[]{
//                response.getStatus(), response.getStatusInfo()});
//            // Body
//            if (response.hasEntity()) 
//                LOG.log(Level.INFO, ">>> testing() {0}", response.readEntity(String.class));
//
//        } finally {
//            LOG.log(Level.INFO, ">>> testing() Closing");
//            response.close(); client.close();
//        }
    }

}
