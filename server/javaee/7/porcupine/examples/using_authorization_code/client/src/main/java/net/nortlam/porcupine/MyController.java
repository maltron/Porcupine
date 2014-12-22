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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.client.AuthorizationCodeGrantController;
import net.nortlam.porcupine.client.exception.ResponseEvent;
import net.nortlam.porcupine.client.exception.UnableToFetchResourceException;
import net.nortlam.porcupine.client.exception.UnableToObtainAccessTokenException;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("my")
@ViewScoped
public class MyController extends AuthorizationCodeGrantController<String> implements Serializable {

    private static final String RESOURCE = "http://localhost:8080/testac/rest/resource";
    private static final Logger LOG = Logger.getLogger(MyController.class.getName());
    
    private String email; // MY GOAL 
    private URI uriResource;
    
    private boolean isAuthentication;
    private String username;
    private String password;

    public MyController() {
        super(String.class, RESOURCE);
    }

    public String getEmail() { // GOAL
        return email;
    }

    public void setEmail(String email) { // GOAL
        this.email = email;
    }
    
    public void setAuthentication(boolean isAuthentication) {
        this.isAuthentication = isAuthentication;
        // Clear if Authentication is empty
        if(!isAuthentication) {
            this.username = null;
            this.password = null;
        }
    }
    
    public boolean isAuthentication() {
        return isAuthentication;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() { // In theory, one must use a login page
        return username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() { // In theory, one must use a login page
        return password;
    }

    @Override
    public String getScope() {
        return "SCOPE_AC";
    }

    @Override
    public String getRedirectURI() {
        return "http://localhost:8080/client/faces/index.xhtml";
    }
    
    public void requestEmail(ActionEvent event) {
        LOG.log(Level.INFO, "requestEmail() Username:{0} Password:{1}",
                new Object[] {getUsername(), getPassword()});
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = null;
        
        try {
            requestResource();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Successfully fetched results", null);
            
        } catch(UnableToObtainAccessTokenException ex) {
            ResponseEvent responseEvent = ex.getEvent();
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Unable to Obtain Access Token",
            String.format("Server responded: %d %s", responseEvent.getCode(),
                    responseEvent.getReason()));
            
        } catch(UnableToFetchResourceException ex) {
            ResponseEvent responseEvent = ex.getEvent();
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Unable to Fetch Information on Resource Server",
            String.format("%d %s", responseEvent.getCode(),
                    responseEvent.getReason()));
        }
        
        context.addMessage("messages", message);
    }
    
    // FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 
    //   FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 

    @Override
    public Response getResponse() {
        return getWebTarget().request()
                .header(HttpHeaders.AUTHORIZATION, getTokenAsBearer()).get();
    }
    
    @Override
    public void setSuccess(String value) {
        setEmail(value);
    }
}
