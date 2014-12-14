package net.nortlam.test.authorization;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.authenticator.FormAuthenticator;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.test.TestCommons;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Mauricio "Maltron" Leal
 */
public class TestAuthorizationCode extends TestCommons implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestAuthorizationCode.class.getName());

    @Before
    public void setupClient() {
        LOG.log(Level.INFO, ">>> [BEFORE] setupClient()");
        try {
            setupClient(new FormAuthenticator(uriAuthorize(), SAMPLE_USERNAME, SAMPLE_PASSWORD));
        } catch(AccessDeniedException ex) {
            LOG.log(Level.SEVERE, "Access Denied Exception");
        }
    }
    
    @After
    public void closeConnections() {
        LOG.log(Level.INFO, ">>> [AFTER] closeConnections()");
        if(response != null) response.close();
        if(client != null) client.close();
    }
    
    
    @Test
    public void testSimpleRequest() {
        LOG.log(Level.INFO, ">>> testSimpleRequest()");
        
        response = client.target(uriAuthorize()).request(MediaType.TEXT_HTML)
                .accept(MediaType.APPLICATION_FORM_URLENCODED).get();
        print("testSimpleRequest()", response);
        
        // It proves the request does exist
        // Considering there is no parameters, then a Bad Request is expected
        assertEquals(response.getStatus(), 400);
    }

    @Test
    public void testRequestingAuthorizationCode() {
        LOG.log(Level.INFO, ">>> testRequestingAuthorizeCode()");
        
        // Using the example on Section 4.1.1 Authorization Request 
        //GET /authorize?
        // response_type=code
        // &client_id=s6BhdRkqt3
        // &state=xyz
        // &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
        //Host: server.example.com        
        
        response = client.target(uriAuthorize())
                // responset_type=code
                .queryParam(OAuth2.PARAMETER_RESPONSE_TYPE, OAuth2.PARAMETER_RESPONSE_TYPE_CODE)
                // client_id=s6BhdRkqt3
                .queryParam(OAuth2.PARAMETER_CLIENT_ID, SAMPLE_CLIENT_ID)
                // state=xyz
                .queryParam(OAuth2.PARAMETER_STATE, "xyz")
                // redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
                .queryParam(OAuth2.PARAMETER_REDIRECT_URI, SAMPLE_REDIRECT_URI)
                // scope=EMAIL
                .queryParam(OAuth2.PARAMETER_SCOPE, SAMPLE_SCOPE)
                .request(MediaType.TEXT_HTML)
                .accept(MediaType.APPLICATION_FORM_URLENCODED).get();
        
        print("testRequestingAuthorizationCode()", response);
        assertEquals(response.getStatus(), 200);
    }
    
    @Test
    public void testAllowingAuthorizationCode() {
        LOG.log(Level.INFO, ">>> testAllowingAuthorizationCode()");
        
        response = client.target(uriAllow())
                .resolveTemplate("clientID", SAMPLE_CLIENT_ID)
                .resolveTemplate("scope", SAMPLE_SCOPE_ID)
                .queryParam(OAuth2.PARAMETER_REDIRECT_URI, SAMPLE_REDIRECT_URI)
                .queryParam(OAuth2.PARAMETER_STATE, "xyz")
                .request(MediaType.APPLICATION_FORM_URLENCODED).get();
        
        print("testAllowingAuthorizationCode()", response);
        assertEquals(response.getStatus(), 303);
        
        String authorizationCode = getCode(response.getLocation());
        assertNotNull(authorizationCode);
        
        // With a Authorization Code, proceed to get an Access Code
        closeConnections(); setupClient();
        
        Form form = new Form();
        form.param(OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE);
        form.param(OAuth2.PARAMETER_CODE, authorizationCode);
        form.param(OAuth2.PARAMETER_REDIRECT_URI, SAMPLE_REDIRECT_URI);
        form.param(OAuth2.PARAMETER_CLIENT_ID, SAMPLE_CLIENT_ID);
        
        response = client.target(uriToken())
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(form));
        print("testAllowingAuthorizationCode()", response);
    }
}
