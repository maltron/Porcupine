/**
 * Copyright 2014 Mauricio "Maltron" Leal <maltron@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.nortlam.porcupine.resource;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.authenticator.Authenticator;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.resource.token.ResourceTokenManagement;

/**
 * Filter used to talk to Authorization Server and obtain an Access Token in
 * order to get access to the content
 *
 * @author Mauricio "Maltron" Leal
 */
public class PorcupineFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(PorcupineFilter.class.getName());

    private ServletContext context;
    private ResourceTokenManagement tokenManagement;
    private Secure secure;

    public PorcupineFilter(ServletContext context, 
                        ResourceTokenManagement tokenManagement, Secure secure) {
        this.context = context; 
        this.tokenManagement = tokenManagement;
        this.secure = secure;
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        System.out.printf(">>> PORCUPINE filter() START START START START >>>\n");
        OAuth2ResourceOperations.debug(request, "Porcupine.filter()");

        LOG.log(Level.INFO, "filter() Is there Authorization Header ?");
        if (!isAuthorizationHeader(request)) {
            redirectNotAuthorize(request);
            return;
        }

        LOG.log(Level.INFO, "filter() Access Token:{0}", request.getHeaderString(HttpHeaders.AUTHORIZATION));
        AccessToken accessToken = new AccessToken();
        try {
            LOG.log(Level.INFO, "filter() Looking for a valid Bearer");
            accessToken.parseAuthorizationBearer(request.getHeaderString(HttpHeaders.AUTHORIZATION));
            
            LOG.log(Level.INFO, "filter() Checking if it has a copy of it");
            accessToken = tokenManagement.retrieveAccessToken(accessToken.getToken());
            
            LOG.log(Level.INFO, "filter() Is it valid ?");
            if(accessToken.isExpired(context)) {
                try {
                    LOG.log(Level.SEVERE, "filter() Token is *EXPIRED*. Deleting it");
                    tokenManagement.delete(accessToken);
                } catch(IOException ex) {
                    LOG.log(Level.SEVERE, "### filter() Unable to delete token"+
                            " IO EXCEPTION:{0}", ex.getMessage());
                }
                redirectNotAuthorize(request); return;
            }
            
            LOG.log(Level.INFO, "filter() !!!!FOUND!!!! Token and it's still valid");
            return;

        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "### filter() No valid Bearer found in the Header AUTHORIZATION");
            redirectNotAuthorize(request); return;
        } catch(IOException ex) {
            LOG.log(Level.WARNING, "### filter() Unable to find Token locally.");
        }

        LOG.log(Level.INFO, "filter() No Token Locally. Checking if it's valid Remotely");
        URI uriCheckPoint = uriCheckEndPoint();
        LOG.log(Level.INFO, "filter() URI Check:{0}", uriCheckPoint);
        Client client = clientInstance(context, request, uriCheckPoint, null, null);
        if(client == null) {
            // Something is wrong with the authentication
            redirectNotAuthorize(request); return;
        }
        WebTarget target = client.target(uriCheckPoint)
                .queryParam(OAuth2.PARAMETER_ACCESS_TOKEN, accessToken.getToken())
                .queryParam(OAuth2.PARAMETER_SCOPE, secure.scope())
                .queryParam(OAuth2.PORCUPINE_PARAMETER_GRANT, secure.grant().toString());
        
        Response response = null; 
        try {
            response = target.request(MediaType.APPLICATION_JSON).get();
            if(response.getStatus() == Response.Status.OK.getStatusCode()) {
                // Good, everything ok with the Token. Store it.
                LOG.log(Level.INFO, "filter() Token is valid. Storing");
                accessToken = response.readEntity(AccessToken.class);
                tokenManagement.store(accessToken);
                // Add Token to the Authorization Header
                addAuthorizationHeader(request, accessToken);
                
            } else {
                // Something is wrong with the validation 
                LOG.log(Level.WARNING, "filter() Access Token is not valid");
                redirectNotAuthorize(request); return;
            }
                
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "### filter() Unable to store Access Token"+
                    " IO EXCEPTION:{0}", ex.getMessage());
            redirectNotAuthorize(request); return;
            
        } finally {
            if(response != null) response.close(); client.close();
        } 
        
                
        LOG.log(Level.INFO, "filter() Everything checks out. GOOD TO GO.");
                
//        oauth.validateParameters(OAuth2.PARAMETER_ACCESS_TOKEN, oauth.getAccessToken(),
//                OAuth2.PARAMETER_SCOPE, oauth.getScope(),
//                OAuth2.PARAMETER_GRANT_TYPE, oauth.getGrantType() != null ? 
//                                        oauth.getGrantType().toString() : null);

//        MultivaluedMap<String, String> parameters = request.getUriInfo().getQueryParameters();
//        
//        List<String> error = parameters.get(OAuth2.PARAMETER_ERROR);
//        List<String> error_description = parameters.get(OAuth2.PARAMETER_ERROR_DESCRIPTION);
//        if(error != null && error_description != null) {
//            LOG.log(Level.WARNING, "filter() An error was detected. Redirecting to Error Page");
//            OAuth2ResourceOperations.redirectErrorPage(context, request);
//            return;
//        }
//
//        // Handles the Grant, depending on what was specified on
//        // the Secure Annotation
//        handleGrant.handle(context, request);
        System.out.printf(">>> PORCUPINE filter() END END END END END END >>>\n");
    }

//    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
//    //   DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
//    protected void debug(ContainerRequestContext request, String method) {
//        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n", method);
//        UriInfo info = request.getUriInfo();
//        System.out.printf("[CLIENT] Absolute path: %s\n", info != null ? info.getAbsolutePath() : "NULL");
//        System.out.printf("[CLIENT] Base URI:      %s\n", info != null ? info.getBaseUri() : "NULL");
//        System.out.printf("[CLIENT] Path:          %s\n", info != null ? info.getPath() : "NULL");
//        System.out.printf("[CLIENT] Request URI:    %s\n", info != null ? info.getRequestUri() : "NULL");
//        for(Map.Entry<String, List<String>> entry: request.getHeaders().entrySet()) {
//            String key = entry.getKey();
//            for(String value: entry.getValue())
//                System.out.printf("[CLIENT] Header %s = %s\n", key, value);
//        }
//        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n", method);
//    }
    private boolean isAuthorizationHeader(ContainerRequestContext request) {
        return request.getHeaderString(HttpHeaders.AUTHORIZATION) != null;
    }
    
    protected void addAuthorizationHeader(ContainerRequestContext request, AccessToken accessToken) {
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, accessToken.toStringAuthorizationBearer());
    }

    private void redirectNotAuthorize(ContainerRequestContext request) {
        request.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    /**
     * Return an instance o JAX-RS 2 Client, using the necessary Authenticator
     * in order to obtain access to the Authorization Server */
    private Client clientInstance(ServletContext context, 
                            ContainerRequestContext request,  URI uri, 
                                            String username, String password) {

//        if(!InitParameter.isParameterAuthenticator(context)) 
//            return ClientBuilder.newClient();
        
        Authenticator authenticator = null;
        try {
            // Must be either Authenticator.FORM or Authenticator.BASIC
            authenticator = InitParameter.parameterAuthenticator(uri, context, 
                                                            username, password);
        } catch(AccessDeniedException ex) {
            LOG.log(Level.SEVERE, "clientInstance() Unable to Perform authentication "+
                    " on Authorization Server");
            redirectNotAuthorize(request);
            return null;
        }
        
        ClientBuilder builder = ClientBuilder.newBuilder();
        // Is there any form of Authentication used ?
        if(authenticator != null) builder.register(authenticator);
        // Is there SSL Enabled ?
        if(isSSL(uri)) builder.sslContext(InitParameter.createContext(context));
        
        return builder.build();
    }
    
    private URI uriCheckEndPoint() {
        return InitParameter.uriCheckEndpoint(context);
    }
    
    private boolean isSSL(URI uri) {
        return uri.getScheme().equals("https");
    }
}
