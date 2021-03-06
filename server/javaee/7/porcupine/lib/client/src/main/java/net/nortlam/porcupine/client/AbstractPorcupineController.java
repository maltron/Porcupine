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
package net.nortlam.porcupine.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import net.nortlam.porcupine.client.exception.ResponseEvent;
import net.nortlam.porcupine.client.exception.UnableToFetchResourceException;
import net.nortlam.porcupine.client.exception.UnableToObtainAccessTokenException;
import net.nortlam.porcupine.client.token.ClientTokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.authenticator.BasicAuthenticator;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 * Provides all the methods to obtain a certain resource
 *
 * @author Mauricio "Maltron" Leal */
public abstract class AbstractPorcupineController<T> 
                                    implements Serializable, FetchResource<T> {

    private static final Logger LOG = Logger.getLogger(AbstractPorcupineController.class.getName());
    
    @Inject
    protected ClientTokenManagement tokenManagement;
    
    private Client client;
    private Class<T> typeParameterClass; // Needs to setup before using this Class
    private URI uriResource;
    private String errorValue;
    
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

    /**
     * By accessing this method, it will detect an error redirect to this page */
    public void setError(String errorValue) {
        LOG.log(Level.WARNING, "setError() {0}", errorValue);
        this.errorValue = errorValue; // Not really used
        if(errorValue == null) return;
        
        Map<String, String> parameters = FacesContext.getCurrentInstance()
                                .getExternalContext().getRequestParameterMap();
        String error = parameters.get(OAuth2.PARAMETER_ERROR);
        String errorDescription = parameters.get(OAuth2.PARAMETER_ERROR_DESCRIPTION);
        String errorURI = parameters.get(OAuth2.PARAMETER_ERROR_URI);
        String state = parameters.get(OAuth2.PARAMETER_STATE);
        
        redirectErrorPage(error, errorDescription, errorURI, state);
    }
    
    public String getError() {
        return errorValue;  // Not really used
    }
    
    protected Client clientInstance(URI uri) {
        return clientInstance(getUsername(), getPassword(), uri); // Assuming no Authentication
                    // at the endpoints are not necessary
    }
    
    /**
     * THIS MUST CHANGE: Authentication it will be perform only on Resource Server
     * *NOT* on Authorization Server 
     * 
     * So far, only the BASIC Authentication it's provided and it's activate
     * if username and password are provided
     */
    protected Client clientInstance(String username, String password, URI uri){
        ClientBuilder builder = ClientBuilder.newBuilder();
        
        // BASIC or FORM Authentication ?
        if(username != null && password != null) {
            LOG.log(Level.INFO, "clientInstance() Username:{0} Password:{1}",
                    new Object[] {username, password});
            builder.register(new BasicAuthenticator(username, password));
        }
        
        // SSL ?
        if(isSSL(uri)) {
            LOG.log(Level.INFO, "clientInstance() SSL Enabled");
            builder.sslContext(InitParameter.createContext(getContext()));
        }
        
        // No Authenticator needed
        return builder.build();
    }
    
    protected boolean isSSL(URI uri) {
        return uri.getScheme().equals("https");
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
    
    protected AccessToken requestAccessToken(Grant grant, String authorizationCode) 
                                            throws UnableToObtainAccessTokenException {
        return requestAccessToken(grant, null, null, authorizationCode, null);
    }
    
    protected AccessToken requestAccessToken(Grant grant, String scope,
            AccessToken expiredAccessToken) throws UnableToObtainAccessTokenException {
        return requestAccessToken(grant, scope, expiredAccessToken, null, null);
    }
    
    protected AccessToken requestAccessToken(Grant grant, String scope, 
            AccessToken expiredAccessToken, 
                                String authorizationCode,String redirectURI) 
                                      throws UnableToObtainAccessTokenException {
        ServletContext context = getContext();
        
        LOG.log(Level.INFO, "requestAccessToken() Grant:{0} Scope:{1} "+
                "Username:{2} Password:{3} AuthorizationCode:{4} RedirectURI:{5}", 
                new Object[] {grant, scope, getUsername(), getPassword(), 
                                                 authorizationCode, redirectURI});
        URI tokenEndpoint = InitParameter.uriTokenEndpoint(context);
        
        // No authentication it will be used against the Authorization Server
        Client client = clientInstance(null, null, tokenEndpoint);
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
        
        Response response = null; 
        try {
            LOG.log(Level.INFO, ">>> [CLIENT] requestAccessToken() Posting....");
            response = targetTokenEndpoint.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(form));
            
            int code = response.getStatus();
            String body = null;
            String reason = response.getStatusInfo().getReasonPhrase();
            
            if(code == Response.Status.OK.getStatusCode()) 
                accessToken = response.readEntity(AccessToken.class);
            else {
                body = response.hasEntity() ? response.readEntity(String.class) : null;
                LOG.log(Level.SEVERE, "requestAccessToken() Response:{0}", body);
                
                // Generate an event
                ResponseEvent event = new ResponseEvent(code, reason, body);
                throw new UnableToObtainAccessTokenException(event);
            }
            
        } finally {
            if(response != null) response.close(); client.close();
        }
        
        // Redirect to some other page, only if the Grant has a redirect page
        // In case of error, the page must be prepared to handle this thought 
        // the method setError
//        if(!success && (grant == Grant.AUTHORIZATION_CODE || grant == Grant.IMPLICIT)) 
//            redirectErrorPage(null);
        
        // If returns NULL, it must be handle it
        return accessToken;
    }

    protected void redirectErrorPage(String error, String errorDescription) {
        redirectErrorPage(error, errorDescription, null, null);
    }

    protected void redirectErrorPage(String error, String errorDescription, String errorURI, 
            String state) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        
        UriBuilder builder = UriBuilder.fromUri(getErrorPage());
        if(error != null) builder.queryParam(OAuth2.PARAMETER_ERROR, error);
        if(errorDescription != null)
            builder.queryParam(OAuth2.PARAMETER_ERROR_DESCRIPTION, errorDescription);
        if(errorURI != null)
            builder.queryParam(OAuth2.PARAMETER_ERROR_URI, errorURI);
        if(state != null)
            builder.queryParam(OAuth2.PARAMETER_STATE, state);
        
        // Redirect the whole content using the parameters 
        try {
            context.redirect(builder.build().toASCIIString());
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "### errorPage() IO EXCEPTION:{0}", ex.getMessage());
        }
        
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
    }
    
    private String getErrorPage() {
        String errorPage = InitParameter.parameterErrorPage(getContext());
        
        return errorPage != null ? errorPage : getRedirectURI();
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
    
    protected ServletContext getContext() {
        return (ServletContext)FacesContext.getCurrentInstance()
                                .getExternalContext().getContext();
    }
    
    protected String debug() {
        return debug(null);
    }
    
    protected String debug(Date expiration) {
        SimpleDateFormat dateFormat = InitParameter.parameterDateFormat(getContext());
        TimeZone timeZone = InitParameter.parameterTimeZone(getContext());
        Locale locale = InitParameter.parameterLocale(getContext());
        
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        if(expiration != null) calendar.setTime(expiration);
        
        return dateFormat.format(calendar.getTime());
    }
    
    public void setTypeParameterClass(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }
    
    /**
     * Request the content from the Resource Server */
    protected abstract void requestResource() 
            throws UnableToObtainAccessTokenException, UnableToFetchResourceException;

    protected void performFetchResource() throws UnableToFetchResourceException {
        URI resource = getResource();
        Response response = getResponse();
        
        try {
            int code = response.getStatus();
            String reason = response.getStatusInfo().getReasonPhrase();

            // It's easy to forget to implement that
            if(typeParameterClass() == null) 
            throw new UnableToFetchResourceException(
                    new ResponseEvent(-1, "performFetchResource(): typeParameterClass()"+
                            " is NULL. Be sure to implemented.", null));
            T result = response.readEntity(typeParameterClass());
            
            if(code == Response.Status.OK.getStatusCode()) {
                setSuccess(result);
            } else {
                // Generate an Event with some information regarding
                // the problem
                ResponseEvent event = new ResponseEvent(code, reason, 
                                    result != null ? result.toString() : null);
                throw new UnableToFetchResourceException(event);
            }
            
        } finally {
            if(response != null) response.close();
            if(this.client != null) this.client.close();
        }
    }
    
    protected WebTarget getWebTarget() {
        URI resource = getResource();
        this.client = clientInstance(resource); // Username and Password will activate the authentication
        return this.client.target(resource);
    }
    
    protected String getTokenAsBearer() {
        URI resource = getResource();
        AccessToken token = tokenManagement.retrieve(resource);
        return token.toStringAuthorizationBearer();
    }
    
    protected void setResource(String resource) {
        try {
            uriResource = new URI(resource);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "setResource() URI SYNTAX EXCEPTION:{0}",
                                                            ex.getMessage());
        }
    }
    
    // FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 
    //  FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 
    @Override
    public URI getResource() {
        return uriResource;
    }

    @Override
    public Class<T> typeParameterClass() {
        return typeParameterClass; 
    }
    
    @Override
    public abstract Response getResponse();
    
    @Override
    public abstract void setSuccess(T t);
}
