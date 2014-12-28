/*
 * 
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
 * 
 */
package net.nortlam.porcupine.examples;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.client.ClientCredentialsController;
import net.nortlam.porcupine.client.exception.ResponseEvent;
import net.nortlam.porcupine.client.exception.UnableToFetchResourceException;
import net.nortlam.porcupine.client.exception.UnableToObtainAccessTokenException;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("my")
@RequestScoped
public class MyController extends ClientCredentialsController<Person> {

    private static final Logger LOG = Logger.getLogger(MyController.class.getName());
    private static final String RESOURCE = "https://localhost:8443/testcc/rest/resource";
    
    private URI uriResource;
    private Person person; // MY GOAL

    public MyController() {
        super(Person.class);
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }
    
    public void setFirstName(String firstName) {
        // Nothing happen
    }
    
    public String getFirstName() {
        return person != null ? person.getFirstName() : null;
    }
    
    public void setLastName(String lastName) {
        // Nothing happen
    }
    
    public String getLastName() {
        return person != null ? person.getLastName() : null;
    }
    
    public void requestPerson(ActionEvent event) {
        try {
            requestResource();
            
        } catch(UnableToObtainAccessTokenException ex) {
            ResponseEvent responseEvent = ex.getEvent();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Unable to Obtain Access Token",
            String.format("Server responded: %d %s", responseEvent.getCode(),
                    responseEvent.getReason()));
            FacesContext.getCurrentInstance().addMessage("messages", error);
            
        } catch(UnableToFetchResourceException ex) {
            ResponseEvent responseEvent = ex.getEvent();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Unable to Fetch Information on Resource Server",
            String.format("%d %s", responseEvent.getCode(),
                    responseEvent.getReason()));
            FacesContext.getCurrentInstance().addMessage("messages", error);
        }
    }
    
    // CLIENT CREDENTIALS CONTROLLER CLIENT CREDENTIALS CONTROLLER CLIENT CREDENTIALS CONTROLLER 
    //   CLIENT CREDENTIALS CONTROLLER CLIENT CREDENTIALS CONTROLLER CLIENT CREDENTIALS CONTROLLER 

//    @Override
//    public String getUsername() {
//        return "maltron@gmail.com";
//    }
//
//    @Override
//    public String getPassword() {
//        return "maltron";
//    }

    @Override
    public String getScope() {
        return "REAL_NAME";
    }
    
    // FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 
    //  FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 

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

    /**
     * No need to add the Authorization Header. The framework it will add it
     * automatically for you */
    @Override
    public Response getResponse() { 
        return getWebTarget().request(MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, getTokenAsBearer())
                .post(Entity.entity(Person.class, MediaType.WILDCARD_TYPE));
    }

    @Override
    public void setSuccess(Person person) {
        setPerson(person);
    }
}
