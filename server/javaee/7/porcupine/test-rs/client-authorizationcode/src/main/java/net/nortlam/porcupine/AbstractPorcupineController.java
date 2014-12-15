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

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.authenticator.Authenticator;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 * Provides all the methods to obtain a certain resource
 *
 * @author Mauricio "Maltron" Leal */
public abstract class AbstractPorcupineController implements Serializable {

    private static final Logger LOG = Logger.getLogger(AbstractPorcupineController.class.getName());
    
    public AbstractPorcupineController() {
    }

    public abstract String getUsername();
    public abstract String getPassword();
    
    // Properties important for requesting the information
    public abstract String getScope();
    public abstract String getRedirectURI();
    
    /**
     * Optional. Can be overridden in order to provide an state */
    public String getState() {
        return null;
    }
    
    protected Client clientInstance(URI uri) {
        return clientInstance(uri, null, null); // Assuming no Authentication
                    // at the endpoints are not necessary
    }
    
    protected Client clientInstance(URI uri, String username, String password){
        Authenticator authenticator = null;
        try {
            authenticator = InitParameter.parameterAuthenticator(uri, 
                                                    getContext(), username, password);
        } catch(AccessDeniedException ex) {
            LOG.log(Level.SEVERE, "clientInstance() Unable to authenticate into"+
                    " AuthorizationServer. AccessDeniedException:{0}", ex.getMessage());
        }
        
        Client client = null;
        if(authenticator != null) 
            client = ClientBuilder.newClient().register(authenticator);
        else client = ClientBuilder.newClient();
        
        
        return client;
    }
    
    protected void redirectAuthorizationServer(Grant grant) {
        
        URI uri = null;
        switch(grant) {
            case AUTHORIZATION_CODE: 
                uri = requestAuthorizationCode(); break;
//                
//            case IMPLICIT: uri = requestAccessToken(context, request, scope); break;
        }
        
        ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
        try {
            external.redirect(uri.toASCIIString());
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "### IO EXCEPTION@redirectAuthorizationServer():{0}",
                    ex.getMessage());
        }
    }
    
    protected URI requestAuthorizationCode() {
        return requestAuthorizationCode(getScope(), getRedirectURI(), getState());
    }
    
    protected URI requestAuthorizationCode(String scope, String redirectURI, String state) {
    //      http://localhost:8080/server/oauth2/authorize?
    //      response_type=code
    //      &client_id=cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=
    //      &scope=EMAIL
    //      &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fclient
        
        UriBuilder builder = UriBuilder.fromUri(authorizeEndpoint());
        builder.queryParam(OAuth2.PARAMETER_RESPONSE_TYPE, OAuth2.PARAMETER_RESPONSE_TYPE_CODE);
        builder.queryParam(OAuth2.PARAMETER_CLIENT_ID, getClientID());
        if(scope != null) builder.queryParam(OAuth2.PARAMETER_SCOPE, scope);
        if(redirectURI != null) builder.queryParam(OAuth2.PARAMETER_REDIRECT_URI, redirectURI);
        if(state != null) builder.queryParam(OAuth2.PARAMETER_STATE, state);
        
        return builder.build();
    }
    
    protected AccessToken requestAccessToken(Grant grant, String authorizationCode) {
        return requestAccessToken(grant, null, null, authorizationCode, null);
    }
    
    protected AccessToken requestAccessToken(Grant grant, String scope,
            AccessToken expiredAccessToken) {
        return requestAccessToken(grant, scope, expiredAccessToken, null, null);
    }
    
    protected AccessToken requestAccessToken(Grant grant, String scope, 
            AccessToken expiredAccessToken, 
                                String authorizationCode,String redirectURI) {
        ServletContext context = getContext();
        
        LOG.log(Level.INFO, "requestAccessToken() Grant:{0} Scope:{1} "+
                "Username:{2} Password:{3} AuthorizationCode:{4} RedirectURI:{5}", 
                new Object[] {grant, scope, getUsername(), getPassword(), 
                                                 authorizationCode, redirectURI});
        URI tokenEndpoint = InitParameter.uriTokenEndpoint(context);
        
        Client client = clientInstance(tokenEndpoint, getUsername(), getPassword());
        WebTarget targetTokenEndpoint = client.target(tokenEndpoint);
        
        Form form = new Form();
        switch(grant) {
            case RESOURCE_OWNER_PASSWORD_CREDENTIALS:
                form.param(OAuth2.PARAMETER_GRANT_TYPE,
                        OAuth2.PARAMETER_GRANT_TYPE_PASSWORD);
                break;
            case CLIENT_CREDENTIALS:
                form.param(OAuth2.PARAMETER_GRANT_TYPE, 
                        OAuth2.PARAMETER_GRANT_TYPE_CLIENT_CREDENTIALS);
                break;
            case REFRESH_TOKEN:
                form.param(OAuth2.PARAMETER_GRANT_TYPE, 
                        OAuth2.PARAMETER_GRANT_TYPE_REFRESH_TOKEN);
                form.param(OAuth2.PARAMETER_REFRESH_TOKEN, 
                                expiredAccessToken.getRefreshToken());
                break;
            case AUTHORIZATION_CODE:
                form.param(OAuth2.PARAMETER_GRANT_TYPE, 
                                OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE);
                form.param(OAuth2.PARAMETER_CODE, authorizationCode);
                
                if(getRedirectURI() != null)
                    form.param(OAuth2.PARAMETER_REDIRECT_URI, getRedirectURI());
                form.param(OAuth2.PARAMETER_CLIENT_ID, 
                                        InitParameter.parameterClientID(context));
                break;
        }

        // For used only on RESOURCE_OWNER_PASSWORD_CREDENTIALS
        if(getUsername() != null) form.param(OAuth2.PARAMETER_USERNAME, getUsername());
        // For used only on RESOURCE_OWNER_PASSWORD_CREDENTIALS
        if(getPassword() != null) form.param(OAuth2.PARAMETER_PASSWORD, getPassword());
        
        // One of the most importants parameters
        if(scope != null) form.param(OAuth2.PARAMETER_SCOPE, scope);
        
        AccessToken accessToken = null;
        Response response = null; boolean success = true;
        try {
            LOG.log(Level.INFO, ">>> [CLIENT] requestAccessToken() Posting....");
            response = targetTokenEndpoint.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(form));
            
            if(response.getStatus() == Response.Status.OK.getStatusCode()) 
                accessToken = response.readEntity(AccessToken.class);
            else {
                success = false;
                LOG.log(Level.SEVERE, "requestAccessToken() Response:{0}", 
                        response.readEntity(String.class));
            }
            
        } finally {
            if(response != null) response.close(); client.close();
        }
        
        // Redirect to some other page, only if the Grant has a redirect page
        if(!success && (grant == Grant.AUTHORIZATION_CODE || grant == Grant.IMPLICIT)) 
            redirectErrorPage(null);
        
        // If returns NULL, it must be handle it
        return accessToken;
    }

    protected void redirectErrorPage(String clientError) {
        // TO DO
    }
    
    
    protected URI errorPage(String clientError) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//        context.redirect("http://localhost");
        
//        // Get the Error Page from the Configuration
//        String pageError = InitParameter.parameterErrorPage(context);
//        // Or use the Current URL as a basis
//        if(pageError == null) 
//            pageError = request.getUriInfo().getAbsolutePath().toASCIIString();
//        
//        LOG.log(Level.INFO, "errorPage() URI:{0}", pageError);
//        return clientError == null ? URIBuilder.buildURIMultivaluedMap(
//                 pageError, request.getUriInfo().getQueryParameters()) :
//            URIBuilder.buildURI(pageError, OAuth2.PARAMETER_ERROR_DESCRIPTION, clientError);
        return null;
    }
    
    private String getClientID() {
        return InitParameter.parameterClientID(getContext());
    }
    
    private String getClientSecret() {
        return InitParameter.parameterClientSecret(getContext());
    }
    
    private URI authorizeEndpoint() {
        return InitParameter.uriAuthorizeEndpoint(getContext());
    }
    
    private URI tokenEndpoint() {
        return InitParameter.uriTokenEndpoint(getContext());
    }
    
    private ServletContext getContext() {
        return (ServletContext)FacesContext.getCurrentInstance()
                                .getExternalContext().getContext();
    }
}
