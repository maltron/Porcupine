package net.nortlam.test.authorization;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.test.TestCommons;
import org.junit.Test;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class TestRequestAccessToken extends TestCommons implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestRequestAccessToken.class.getName());

    @Test
    public void testRequestAccessToken() {
        Client client = ClientBuilder.newClient().register(authenticatorForm());
        WebTarget target = client.target(uriToken());
        System.out.printf("%s\n",target.getUri());
        
        String authorizationCode = "cG9yY3VwaW5lLWF1dGhvcml6YXRpb24tY29kZS01ZWZiMzRmYy05OWJiLTRmM2QtYmI2MS0xNzY5M2ZmMGE2NWI=";
        
        Form form = new Form();
        form.param(OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE);
        form.param(OAuth2.PARAMETER_CODE, authorizationCode);
        form.param(OAuth2.PARAMETER_REDIRECT_URI, "http://localhost:8080/client/resource/person");
        form.param(OAuth2.PARAMETER_CLIENT_ID, SAMPLE_CLIENT_ID);
        
        Response response = null;
        try {
            response = target
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(form));
            print("testRequestAccessToken", response);
//            LOG.log(Level.INFO, ">>> testRequestAccessToken() Response: {0} {1}",
//                    new Object[] { response.getStatus(), response.getStatusInfo()});
//            LOG.log(Level.INFO, ">>> testRequestAccessToken() BODY:{0}", response.hasEntity() ? response.readEntity(String.class) : "NULL");
//            if(response.getStatus() == 200) {
//                if(response.hasEntity()) {
//                    AccessToken token = response.readEntity(AccessToken.class);
//                }
//            }
        } finally {
            if(response != null) response.close(); client.close();
        }
    }

}
