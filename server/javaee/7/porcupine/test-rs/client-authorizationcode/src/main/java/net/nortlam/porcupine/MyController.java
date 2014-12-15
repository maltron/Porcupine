package net.nortlam.porcupine;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("my")
@RequestScoped
public class MyController extends AbstractPorcupineController implements Serializable {

    private static final String RESOURCE = "http://localhost:8080/testac/rest/resource";
    private static final Logger LOG = Logger.getLogger(MyController.class.getName());
    
    @EJB
    private TokenStorage tokenStorage;
    
    private String authorizationCode;
    private String email;

    public MyController() {
    }

    @Override
    public String getUsername() {
        return "maltron@gmail.com";
    }

    @Override
    public String getPassword() {
        return "maltron";
    }

    @Override
    public String getScope() {
        return "EMAIL";
    }

    @Override
    public String getRedirectURI() {
        return "http://localhost:8080/client/faces/index.xhtml";
    }
    

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getResource() {
        return RESOURCE;
    }
    
    public void setCode(String code) {
        this.authorizationCode = code;
        LOG.log(Level.INFO, "setCode(){0}", authorizationCode);
        
        // Good, if we've got the code, we're half way there
        // Requesting an Access token then
        LOG.log(Level.INFO, "setCode() Requesting a new Access Token");
        AccessToken accessToken = requestAccessToken(Grant.AUTHORIZATION_CODE, authorizationCode);
        
        LOG.log(Level.INFO, "setCode() Sucessfull acquired. Storing");
        tokenStorage.put(RESOURCE, accessToken);
        
        // Requesting the Resource intented
        requestEmail(null);
    }
    
    public String getCode() {
        return authorizationCode;
    }
    
    public void requestEmail(ActionEvent event) {
        LOG.log(Level.INFO, "requestEmail()");
        
        // Is there any AccessToken avaliable ?
        LOG.log(Level.INFO, "requestEmail() Is there any Acess Tokens available for:{0}", RESOURCE);
        AccessToken accessToken = tokenStorage.get(RESOURCE);
        if(accessToken != null) {
            LOG.log(Level.INFO, "requestEmail() Yes, there is. Fetch it");
            // There is an Access Token avaliable. Fetch Resource
            this.email = fetchResource(accessToken);
            return;
        }
        
        // No, there isn't any Access Token avaliable yet. 
        // Redirect to Authorize Server in order to get one
        LOG.log(Level.INFO, "requestEmail() No, Redirecting to Authorization Server");
        redirectAuthorizationServer(Grant.AUTHORIZATION_CODE);
    }
    
    private String fetchResource(AccessToken accessToken) {
        URI uriResource = UriBuilder.fromUri(RESOURCE).build();
        Client client = clientInstance(uriResource);
        WebTarget target = client.target(uriResource);
        
        Response response = null; String content = null;
        try {
            response = target.request().header(
            // Access Token
            HttpHeaders.AUTHORIZATION, accessToken.toStringAuthorizationBearer()).get();
            
            if(response.getStatus() == Response.Status.OK.getStatusCode()) 
                content = response.readEntity(String.class);
            else {
                content = null;
                LOG.log(Level.SEVERE, "ERROR\n\n{0}", response.readEntity(String.class));
            }
            
        } finally {
            if(response != null) response.close(); client.close();
        }
        
        return content;
    }
    
//    public void takeMeToGoogle(ActionEvent event) {
//        try {
//            FacesContext.getCurrentInstance().getExternalContext().redirect("http://google.com");
//        } catch(IOException ex) {
//            LOG.log(Level.SEVERE, "IO EXCEPTION:{0}", ex.getMessage());
//        }
//    }
}
