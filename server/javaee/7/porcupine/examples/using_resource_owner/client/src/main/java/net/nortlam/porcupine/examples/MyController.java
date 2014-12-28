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
import net.nortlam.porcupine.client.ResourceOwnerPasswordCredentialsController;
import net.nortlam.porcupine.client.exception.ResponseEvent;
import net.nortlam.porcupine.client.exception.UnableToFetchResourceException;
import net.nortlam.porcupine.client.exception.UnableToObtainAccessTokenException;

/**
 * @author Mauricio "Maltron" Leal */
@Named("my")
@RequestScoped
public class MyController extends ResourceOwnerPasswordCredentialsController<Document> {

    private static final Logger LOG = Logger.getLogger(MyController.class.getName());
    
    private static final String RESOURCE = "https://localhost:8443/testropc/rest/resource";
    private URI uriResource;
    
    private Document document;

    public MyController() {
        super(Document.class);
    }
    
    public void setDocument(Document document) {
        this.document = document;
    }
    
    public Document getDocument() {
        return document;
    }
    
    public void setDocumentID(String documentID) {
        // NOTHING TO DO
    }
    
    public String getDocumentID() {
        return document != null ? document.getID() : null;
    }
    
    public void setDocumentName(String documentName) {
        // NOTHING TO DO
    }
    
    public String getDocumentName() {
        return document != null ? document.getName() : null;
    }

    @Override
    public String getUsername() {
        return "hello@hello.com";
    }

    @Override
    public String getPassword() {
        return "123";
    }

    @Override
    public String getScope() {
        return "DOCUMENTS";
    }
    
    public void requestDocument(ActionEvent evet) {
        try {
            requestResource();
            
        } catch(UnableToObtainAccessTokenException ex) {
            ResponseEvent responseEvent = ex.getEvent();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Unable to Obtain Access Token",
            String.format("%d %s", responseEvent.getCode(),
                    responseEvent.getReason()));
            FacesContext.getCurrentInstance().addMessage("messages", error);
            
        } catch(UnableToFetchResourceException ex) {
            ResponseEvent responseEvent = ex.getEvent();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Unable to Fetch Information on Resource Server",
            String.format("Server responded: %d %s", responseEvent.getCode(),
                    responseEvent.getReason()));
            FacesContext.getCurrentInstance().addMessage("messages", error);
        }
    }
    
    // RESOURCE SERVER RESOURCE SERVER RESOURCE SERVER RESOURCE SERVER RESOURCE SERVER 
    //  RESOURCE SERVER RESOURCE SERVER RESOURCE SERVER RESOURCE SERVER RESOURCE SERVER 

    @Override
    public URI getResource() {
        if(uriResource != null) return uriResource;
        
        try {
            uriResource = new URI(RESOURCE);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "URI SYNTAX EXCEPTION:getResource():{0}",
                    ex.getMessage());
        }
        
        return uriResource;
    }

    @Override
    public Response getResponse() {
        return getWebTarget().request(MediaType.APPLICATION_JSON)
                // IMPORTANT: It won't work without it
                .header(HttpHeaders.AUTHORIZATION, getTokenAsBearer())
                .post(Entity.entity(Document.class, MediaType.APPLICATION_JSON));
    }

    @Override
    public void setSuccess(Document document) {
        setDocument(document);
    }
}
