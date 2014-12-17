/**
 * Copyright 2014 Mauricio "Maltron" Leal <maltron@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nortlam.porcupine;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.client.AuthorizationCodeGrantController;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("my")
@RequestScoped
public class MyController extends AuthorizationCodeGrantController<String> implements Serializable {

    private static final String RESOURCE = "http://localhost:8080/testac/rest/resource";
    private static final Logger LOG = Logger.getLogger(MyController.class.getName());
    
    private String email; // MY GOAL 
    private URI uriResource;

    public MyController() {
    }


    public String getEmail() { // GOAL
        return email;
    }

    public void setEmail(String email) { // GOAL
        this.email = email;
    }

    @Override
    public String getUsername() { // In theory, one must use a login page
        return "maltron@gmail.com";
    }

    @Override
    public String getPassword() { // In theory, one must use a login page
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
    
    public void requestEmail(ActionEvent event) {
        LOG.log(Level.INFO, "requestEmail()");
        requestResource();
    }
    
    // FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 
    //   FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 

    @Override
    public URI getResource() {
        if(uriResource != null) return uriResource;
        
        try {
            uriResource = new URI(RESOURCE);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "URI SYNTAX EXCEPTION:{0}", ex.getMessage());
        }
        
        return uriResource;
    }
    
    @Override
    public Response getResponse() {
        return getWebTarget().request()
             // VERY IMPORTANT: Without, it won't be possible to fetch Resource
             .header(HttpHeaders.AUTHORIZATION, getTokenAsBearer())
             .get();
    }
    
    @Override
    public Class<String> typeParameterClass() {
        return String.class;
    }

    @Override
    public void setSuccess(String value) {
        setEmail(value);
    }

    @Override
    public void setFailture(String value) {
        LOG.log(Level.SEVERE, "### FAILURE:{0}", value);
    }
    
    
    
//    public void requestEmail(ActionEvent event) {
//        LOG.log(Level.INFO, "requestEmail()");
//        
//        // Is there any AccessToken avaliable ?
//        LOG.log(Level.INFO, "requestEmail() Is there any Access Tokens available for:{0}", RESOURCE);
//        AccessToken accessToken = tokenStorage.get(RESOURCE);
//        if(accessToken != null) {
//            LOG.log(Level.INFO, "requestEmail() Access Token:{0}", accessToken.getToken());
//            LOG.log(Level.INFO, "requestEmail() NOW:{0} EXPIRATION:{1}",
//            new Object[] {debugExpiration(), debugExpiration(accessToken.getExpiration())});
//            if(!accessToken.isExpired(getContext())) {
//                LOG.log(Level.INFO, "requestEmail() Yes, there is and it still valid. Fetch it");
//                // There is an Access Token avaliable. Fetch Resource
//                this.email = fetchResource(accessToken);
//                LOG.log(Level.INFO, "requestEmail() EMAIL FOUND:{0}", this.email);
//                
//                this.authorizationCode = null;
//                return;
//                
//            } else {
//                LOG.log(Level.INFO, "requestEmail() Access Token is expired. Refreshing");
//                // PENDING: Refreshing 
//                accessToken = requestAccessToken(Grant.REFRESH_TOKEN, getScope(), accessToken);
//                tokenStorage.put(RESOURCE, accessToken);
//                
//                LOG.log(Level.INFO, "requestEmail() Requesting the same content again");
//                requestEmail(null);
//                return;
//            }
//        }
//        
//        // No, there isn't any Access Token avaliable yet. 
//        // Redirect to Authorize Server in order to get one
//        LOG.log(Level.INFO, "requestEmail() No, Redirecting to Authorization Server");
//        redirectAuthorizationServer(Grant.AUTHORIZATION_CODE);
//    }
//    
//    private String fetchResource(AccessToken accessToken) {
//        URI uriResource = UriBuilder.fromUri(RESOURCE).build();
//        Client client = clientInstance(uriResource);
//        WebTarget target = client.target(uriResource);
//        
//        Response response = null; String content = null;
//        try {
//            response = target.request().header(
//            // Access Token
//            HttpHeaders.AUTHORIZATION, accessToken.toStringAuthorizationBearer()).get();
//            
//            if(response.getStatus() == Response.Status.OK.getStatusCode()) 
//                content = response.readEntity(String.class);
//            else {
//                content = null;
//                LOG.log(Level.SEVERE, "ERROR\n\n{0}", response.readEntity(String.class));
//            }
//            
//        } finally {
//            if(response != null) response.close(); client.close();
//        }
//        
//        return content;
//    }
    

//    @Override
//    public void fetchResource() {
//        URI resource = getResource();
//        AccessToken token = tokenManagement.retrieve(resource);
//        
//        Client client = clientInstance(resource);
//        WebTarget target = client.target(resource);
//        
//        Response response = null; 
//        try {
//            response = target.request()
//             // VERY IMPORTANT: Without, it won't be possible to fetch Resource
//             .header(HttpHeaders.AUTHORIZATION, token.toStringAuthorizationBearer())
//             .get();
//            
//            if(response.getStatus() == Response.Status.OK.getStatusCode()) {
//                // SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS 
//                this.email = response.readEntity(String.class);
//            } else {
//                // FAILURE FAILURE FAILURE FAILURE FAILURE FAILURE FAILURE 
//                this.email = null;
//                LOG.log(Level.SEVERE, "FAILURE: Response:{0}", response.readEntity(String.class));
//            }
//            
//        } finally {
//            if(response != null) response.close(); client.close();
//        }
//    }

}
