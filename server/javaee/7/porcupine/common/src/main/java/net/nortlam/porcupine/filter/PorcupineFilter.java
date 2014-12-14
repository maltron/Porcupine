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
package net.nortlam.porcupine.filter;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.authenticator.Authenticator;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.util.URIBuilder;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class PorcupineFilter {
    
    // Ideally, this should be state 
    public static final Grant PORCUPINE_GRANT = Grant.AUTHORIZATION_CODE;
//    public static final String PORCUPINE_AUTHORIZATION_SERVER_SCHEME = "http";
//    public static final String PORCUPINE_AUTHORIZATION_SERVER_HOST = "localhost";
//    public static final int PORCUPINE_AUTHORIZATION_SERVER_PORT = 8080;
//    public static final String PORCUPINE_AUTHORIZATION_ENDPOINT = "/server/oauth2/authorize";
//    public static final String PORCUPINE_AUTHORIZATION_TOKEN_ENDPOINT = "/server/oauth2/token";
//    public static final String PORCUPINE_AUTHORIZATION_CHECK_ENDPOINT = "/server/oauth2/check";
//    public static final String PORCUPINE_CLIENT_ID = "cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=";
//    public static final String PORCUPINE_CLIENT_SECRET = "cG9yY3VwaW5lLWNsaWVudC1zZWNyZXQtYjRmM2QyZmYtYmUxZS00ZjkxLWFiZWQtMjAyYWU1MTRhMDgy";
    public static final String PORCUPINE_SCOPE = "EMAIL";
    // THIS INFORMATION SHOULD BE ASKED OR LEAVE IN A CONFIG/SETUP FILE
//    public static final String SAMPLE_USERNAME = "maltron@gmail.com";
//    public static final String SAMPLE_PASSWORD = "maltron";
//    public static final String PORCUPINE_ERROR_PAGE = "http://localhost:8080/client/error";
    
    private static final Logger LOG = Logger.getLogger(PorcupineFilter.class.getName());
    

    protected Client clientInstance(ServletContext context, 
                                    ContainerRequestContext request,  URI uri) {

        if(!InitParameter.isParameterAuthenticator(context)) 
            return ClientBuilder.newClient();
        
        Authenticator authenticator = null;
        try {
            // Must be either Authenticator.FORM or Authenticator.BASIC
            authenticator = InitParameter.parameterAuthenticator(uri, context);
        } catch(AccessDeniedException ex) {
            LOG.log(Level.SEVERE, "clientInstance() Unable to Perform authentication "+
                    " on Authorization Server");
            redirectErrorPage(context, request, 
                    "clientInstance() Unable to Perform authentication "+
                    " on Authorization Server");
        }
        
        return authenticator != null ? ClientBuilder.newClient()
                                        .register(authenticator) : null;
    }

    /**
     * Check if the Token is valid */ 
    protected boolean isAccessTokenValid(ServletContext context, 
                        ContainerRequestContext request, String accessToken) {
        
        URI uri = InitParameter.uriCheckEndpoint(context);
        Client client = clientInstance(context, request, uri);
        WebTarget checkEndpoint = client.target(uri)
                    .queryParam(OAuth2.PARAMETER_ACCESS_TOKEN, accessToken);
        
        Response response = null; boolean exist = false;
        try {
            response = checkEndpoint.request(MediaType.APPLICATION_JSON).get();
            exist = response.getStatus() == Response.Status.OK.getStatusCode();
            
        } finally {
            if(response != null) response.close(); client.close();
        }
        
        // Anything different from Ok, it means something went wrong
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }
    
    /**
     * Look for the token inside the Bearer */
    protected String parseAccessToken(String authenticationHeader) {
        if(authenticationHeader == null) return null;
        
        return authenticationHeader.substring("Bearer ".length(), authenticationHeader.length());
    }
    
    protected void redirectAuthorizationServer(ServletContext context, 
                                                ContainerRequestContext request) {
        request.abortWith(
                Response.seeOther(
                    requestAuthorizationCode(context, request.getUriInfo())).build());
    }
    
    protected URI requestAuthorizationCode(ServletContext context, UriInfo info) {
    //      http://localhost:8080/server/oauth2/authorize?
    //      response_type=code
    //      &client_id=cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=
    //      &scope=EMAIL
    //      &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fclient
        URI authorizeendpoint = InitParameter.uriAuthorizeEndpoint(context);
        return URIBuilder.buildURI(authorizeendpoint, // Authorization Server URL
                // Parameters
                OAuth2.PARAMETER_RESPONSE_TYPE, OAuth2.PARAMETER_RESPONSE_TYPE_CODE,
                OAuth2.PARAMETER_CLIENT_ID, InitParameter.parameterClientID(context),
                OAuth2.PARAMETER_SCOPE, PORCUPINE_SCOPE,
                OAuth2.PARAMETER_REDIRECT_URI, info.getAbsolutePath().toASCIIString(),
                OAuth2.PARAMETER_STATE, "XYZ");
    }
    
    protected AccessToken requestAccessTokenFromAuthorizationCode(
            ServletContext context, ContainerRequestContext request, String code) {
        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode()");
        AccessToken accessToken = null;
        
        URI tokenendpoint = InitParameter.uriTokenEndpoint(context);
        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() Endpoint:{0}", tokenendpoint);
        
        Client client = clientInstance(context, request, tokenendpoint);
        WebTarget targetTokenendpoint = client.target(tokenendpoint);

        // Required Parameters in case of Authorization Grant Code (Step #2)
        Form form = new Form();
        form.param(OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE);
        form.param(OAuth2.PARAMETER_CODE, code);
        form.param(OAuth2.PARAMETER_REDIRECT_URI, request.getUriInfo()
                                            .getAbsolutePath().toASCIIString());
        form.param(OAuth2.PARAMETER_CLIENT_ID, InitParameter.parameterClientID(context));
        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() FORM:{0}", form);
        
        Response response = null; boolean success = false;
        try {
            LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() POSTING....");
            response = targetTokenendpoint.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(form));
            
            if(success = (response.getStatus() == Response.Status.OK.getStatusCode()))
                accessToken = response.readEntity(AccessToken.class);
            
        } finally {
            if(response != null) response.close(); client.close();
        }
        
        // Something went wrong
        if(!success) redirectErrorPage(context, request);
        
        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() AccessToken is NULL ? {0}",
                accessToken == null);
        return accessToken;
    }    
    
//    protected URI authorizeEndpoint() {
//        return URIBuilder.buildURI(PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_ENDPOINT);
//    }
//    
//    protected URI tokenEndpoint() {
//        return URIBuilder.buildURI(PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_TOKEN_ENDPOINT);
//    }
//    
//    protected URI checkEndpoint() {
//        return URIBuilder.buildURI(PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_CHECK_ENDPOINT);
//    }
    
    protected void redirectErrorPage(ServletContext context, 
                            ContainerRequestContext request, String clientError) {
        request.abortWith(Response.seeOther(errorPage(context, request, clientError)).build());
    }
    
    protected void redirectErrorPage(ServletContext context, ContainerRequestContext request) {
        request.abortWith(Response.seeOther(errorPage(context, request)).build());
    }
    
    protected URI errorPage(ServletContext context, ContainerRequestContext request) {
        return errorPage(context, request, null);
    }
    
    protected URI errorPage(ServletContext context, ContainerRequestContext request, 
                                                            String clientError) {
        // Get the Error Page from the Configuration
        String pageError = InitParameter.parameterErrorPage(context);
        // Or use the Current URL as a basis
        if(pageError == null) 
            pageError = request.getUriInfo().getAbsolutePath().toASCIIString();
        
        LOG.log(Level.INFO, "errorPage() URI:{0}", pageError);
        return clientError == null ? URIBuilder.buildURIMultivaluedMap(
                 pageError, request.getUriInfo().getQueryParameters()) :
            URIBuilder.buildURI(pageError, OAuth2.PARAMETER_ERROR_DESCRIPTION, clientError);
    }
    
    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
    //   DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
    protected void debug(ContainerRequestContext request, String method) {
        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n", method);
        UriInfo info = request.getUriInfo();
        System.out.printf("[CLIENT] Absolute path: %s\n", info != null ? info.getAbsolutePath() : "NULL");
        System.out.printf("[CLIENT] Base URI:      %s\n", info != null ? info.getBaseUri() : "NULL");
        System.out.printf("[CLIENT] Path:          %s\n", info != null ? info.getPath() : "NULL");
        System.out.printf("[CLIENT] Request URI:    %s\n", info != null ? info.getRequestUri() : "NULL");
        for(Map.Entry<String, List<String>> entry: request.getHeaders().entrySet()) {
            String key = entry.getKey();
            for(String value: entry.getValue())
                System.out.printf("[CLIENT] Header %s = %s\n", key, value);
        }
        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n", method);
    }
}
