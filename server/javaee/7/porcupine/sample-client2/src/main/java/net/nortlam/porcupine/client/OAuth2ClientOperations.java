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

import java.io.Serializable;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.nortlam.porcupine.common.Grant;

import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.authenticator.Authenticator;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.util.URIBuilder;
import static net.nortlam.porcupine.filter.PorcupineFilter.PORCUPINE_SCOPE;
/**
 *
 * @author Mauricio "Maltron" Leal */
public class OAuth2ClientOperations implements Serializable {

    private static final Logger LOG = Logger.getLogger(OAuth2ClientOperations.class.getName());

    public OAuth2ClientOperations() {
    }
    
    protected boolean isOAuth2Parameters(ContainerRequestContext request) {
        return !isAuthorizationCode(request) && !isError(request) && !isErrorDescription(request);
    }
    
    protected boolean isAuthorizationCode(ContainerRequestContext request) {
        return request.getUriInfo().getQueryParameters().get(OAuth2.PARAMETER_CODE) != null;
    }
    
    protected boolean isError(ContainerRequestContext request) {
        return request.getUriInfo().getQueryParameters().get(OAuth2.PARAMETER_ERROR) != null;
    }
    
    protected boolean isErrorDescription(ContainerRequestContext request) {
        return request.getUriInfo().getQueryParameters().get(OAuth2.PARAMETER_ERROR_DESCRIPTION) != null;
    }
    
    protected String getAuthorizationCode(ContainerRequestContext request) {
        List<String> code = request.getUriInfo().getQueryParameters().get(OAuth2.PARAMETER_CODE);
        if(code.size() > 1) {
            LOG.log(Level.WARNING, "getAuthorizationCode() More than one <code> has found");
        }
        
        return code.get(0);
    }
    

    /**
     * Return an instance o JAX-RS 2 Client, using the necessary Authenticator
     * in order to obtain access to the Authorization Server */
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
    
//    protected AccessToken requestAccessTokenFromAuthorizationCode(ServletContext context,
//            ContainerRequestContext request, String code) {
//        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode()");
//        AccessToken accessToken = null;
//        
//        URI tokenendpoint = InitParameter.uriTokenEndpoint(context);
//        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() Endpoint:{0}", tokenendpoint);
//        
//        Client client = clientInstance(context, request, tokenendpoint);
//        WebTarget targetTokenendpoint = client.target(tokenendpoint);
//
//        // Required Parameters in case of Authorization Grant Code (Step #2)
//        Form form = new Form();
//        form.param(OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE);
//        form.param(OAuth2.PARAMETER_CODE, code);
//        form.param(OAuth2.PARAMETER_REDIRECT_URI, request.getUriInfo()
//                                            .getAbsolutePath().toASCIIString());
//        form.param(OAuth2.PARAMETER_CLIENT_ID, InitParameter.parameterClientID(context));
//        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() FORM:{0}", form);
//        
//        Response response = null; boolean success = false;
//        try {
//            LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() POSTING....");
//            response = targetTokenendpoint.request(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_FORM_URLENCODED)
//                    .post(Entity.form(form));
//            
//            if(success = (response.getStatus() == Response.Status.OK.getStatusCode()))
//                accessToken = response.readEntity(AccessToken.class);
//            else {
//                LOG.log(Level.SEVERE, "requestAccessTokenFromAuthorizationCode() ERROR:{0}",
//                        response.readEntity(String.class));
//            }
//            
//        } finally {
//            if(response != null) response.close(); client.close();
//        }
//        
//        // Something went wrong
//        if(!success) redirectErrorPage(context, request);
//        
//        LOG.log(Level.INFO, ">>> [CLIENT] requestAccessTokenFromAuthorizationCode() AccessToken is NULL ? {0}",
//                accessToken == null);
//        return accessToken;
//    }    
    
    protected AccessToken requestAccessToken(Grant grant, 
            ServletContext context, ContainerRequestContext request, String authorizationCode) {
        return requestAccessToken(grant, null, null, null, context, request, null, 
                                                                authorizationCode);
    }
    
    protected AccessToken requestAccessToken(Grant grant, String scope,
            ServletContext context, ContainerRequestContext request) {
        return requestAccessToken(grant, scope, null, null, context, request, 
                                                                    null, null);
    }
    
    protected AccessToken requestAccessToken(Grant grant, String scope,
            AccessToken expiredAccessToken, ServletContext context, 
            ContainerRequestContext request) {
        return requestAccessToken(grant, scope, null, null, 
                                    context, request, expiredAccessToken, null);
    }
    
    protected AccessToken requestAccessToken(Grant grant, String scope, 
            String username, String password, ServletContext context,
            ContainerRequestContext request, AccessToken expiredAccessToken,
            String authorizationCode) {
        LOG.log(Level.INFO, "requestAccessToken() Grant:{0} Scope:{1} "+
                "Username:{2} Password:{3} AuthorizationCode:{4}", 
                new Object[] {grant, scope, username, password, authorizationCode});
        URI tokenEndpoint = InitParameter.uriTokenEndpoint(context);
        
        Client client = clientInstance(context, request, tokenEndpoint);
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
                form.param(OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE);
                form.param(OAuth2.PARAMETER_CODE, authorizationCode);
                form.param(OAuth2.PARAMETER_REDIRECT_URI, request.getUriInfo()
                                                    .getAbsolutePath().toASCIIString());
                form.param(OAuth2.PARAMETER_CLIENT_ID, InitParameter.parameterClientID(context));
                break;
        }
        
        if(username != null) form.param(OAuth2.PARAMETER_USERNAME, username);
        if(password != null) form.param(OAuth2.PARAMETER_PASSWORD, password);
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
            redirectErrorPage(context, request);
        
        // If returns NULL, it must be handle it
        return accessToken;
    }
    
    
//    protected AccessToken refreshAccessToken(ServletContext context, 
//            ContainerRequestContext request, AccessToken expiredAccessToken) {
//        LOG.log(Level.INFO, ">>> [CLIENT] refreshAccessToken()");
//        URI tokenendpoint = InitParameter.uriTokenEndpoint(context);
//        
//        Client client = clientInstance(context, request, tokenendpoint);
//        WebTarget targetTokenEndpoint = client.target(tokenendpoint);
//        
//        // Required Parameters in case of Token Refresh 
//        Form form = new Form();
//        form.param(OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_REFRESH_TOKEN);
//        form.param(OAuth2.PARAMETER_REFRESH_TOKEN, expiredAccessToken.getRefreshToken());
//        form.param(OAuth2.PARAMETER_SCOPE, expiredAccessToken.getTokenType());
//        
//        AccessToken accessToken = null;
//        Response response = null; 
//        try {
//            LOG.log(Level.INFO, ">>> [CLIENT] refreshAccessToken() Posting....");
//            response = targetTokenEndpoint.request(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_FORM_URLENCODED)
//                    .post(Entity.form(form));
//            
//            if(response.getStatus() == Response.Status.OK.getStatusCode()) 
//                accessToken = response.readEntity(AccessToken.class);
//            else {
//                LOG.log(Level.SEVERE, "refreshAccessToken() Response:{0}", 
//                        response.readEntity(String.class));
//            }
//            
//        } finally {
//            if(response != null) response.close(); client.close();
//        }
//        
//        // If returns NULL, it must be handle it
//        return accessToken;
//    }

    /**
     * Look for the token inside the Bearer */
    protected String parseAccessToken(String authenticationHeader) {
        if(authenticationHeader == null) return null;
        
        return authenticationHeader.substring("Bearer ".length(), authenticationHeader.length());
    }
    
    protected String fetchParameter(String parameterName, MultivaluedMap<String, String> parameters) {
        List<String> options = parameters.get(parameterName);
        
        // WARNING !!!WARNING !!!WARNING !!!WARNING !!!WARNING !!!WARNING !!!
        if(options != null && options.size() > 1)
            LOG.log(Level.WARNING, "fetchParameter() {0} has more than one value",
                    parameterName);
        
        return options != null ? options.get(0) : null;
    }
    
    protected void addAuthorizationHeader(ContainerRequestContext request, AccessToken accessToken) {
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, accessToken.toStringAuthorizationBearer());
    }

    protected void redirectAuthorizationServer(ServletContext context, 
                   ContainerRequestContext request, Grant grant, String scope) {
        
        URI uri = null;
        switch(grant) {
            case AUTHORIZATION_CODE: uri = requestAuthorizationCode(context, request); 
                                     break;
            case IMPLICIT: uri = requestAccessToken(context, request, scope);
                           break;
        }
        
        // Redirect to some other location, using specific query parameters
        request.abortWith(Response.seeOther(uri).build());
    }
    
    protected URI requestAccessToken(ServletContext context, 
            ContainerRequestContext request, String scope) {
//            GET /authorize?response_type=token&
//            client_id=s6BhdRkqt3&
//            state=xyz&
//            redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
//            Host: server.example.com            

        URI authorizeEndpoint = InitParameter.uriAuthorizeEndpoint(context);
        return URIBuilder.buildURI(authorizeEndpoint, // Authorization Server URL
                OAuth2.PARAMETER_RESPONSE_TYPE, OAuth2.PARAMETER_RESPONSE_TYPE_TOKEN,
                OAuth2.PARAMETER_CLIENT_ID, InitParameter.parameterClientID(context),
                OAuth2.PARAMETER_REDIRECT_URI, request.getUriInfo().getAbsolutePath().toASCIIString(),
                OAuth2.PARAMETER_SCOPE, scope,
                OAuth2.PARAMETER_STATE, "xyz");
    }
    
    protected URI requestAuthorizationCode(ServletContext context, ContainerRequestContext request) {
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
                OAuth2.PARAMETER_REDIRECT_URI, request.getUriInfo()
                                            .getAbsolutePath().toASCIIString(),
                OAuth2.PARAMETER_STATE, "XYZ");
    }
    
    public void redirectNotAuthorize(ContainerRequestContext request) {
        request.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    public static void redirectErrorPage(ServletContext context, ContainerRequestContext request, String clientError) {
        request.abortWith(Response.seeOther(errorPage(context, request, clientError)).build());
    }
    
    public static void redirectErrorPage(ServletContext context, ContainerRequestContext request) {
        request.abortWith(Response.seeOther(errorPage(context, request)).build());
    }
    
    public static URI errorPage(ServletContext context, ContainerRequestContext request) {
        return errorPage(context, request, null);
    }
    
    public static URI errorPage(ServletContext context, ContainerRequestContext request, String clientError) {
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
    public static void debug(ContainerRequestContext request, String method) {
        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n", method);
        UriInfo info = request.getUriInfo();
        System.out.printf("[CLIENT] Absolute path: %s\n", info != null ? info.getAbsolutePath() : "NULL");
        System.out.printf("[CLIENT] Base URI:      %s\n", info != null ? info.getBaseUri() : "NULL");
        System.out.printf("[CLIENT] Path:          %s\n", info != null ? info.getPath() : "NULL");
        System.out.printf("[CLIENT] Request URI:    %s\n", info != null ? info.getRequestUri() : "NULL");
        print("HEADER", request.getHeaders());

//        for(String propertyName: request.getPropertyNames())
//            System.out.printf("[CLIENT] Property %s :%s\n", propertyName, request.getProperty(propertyName));
        
//        for(Map.Entry<String, List<String>> entry: request.getHeaders().entrySet()) {
//            String key = entry.getKey();
//            for(String value: entry.getValue())
//                System.out.printf("[CLIENT] Header %s = %s\n", key, value);
//        }
        
//        if(info.getMatchedResources() != null)
//            for(Object object: info.getMatchedResources())
//                System.out.printf("[CLIENT] Matched Resource: %s\n",object);
//        
//        if(info.getMatchedURIs() != null)
//            for(String string: info.getMatchedURIs())
//                System.out.printf("[CLIENT Matched URI %s\n",string);
//        
//        if(info.getPathParameters() != null)
//            print("Path Parameter", info.getPathParameters());
//        
//        if(info.getPathSegments() != null) {
//            for(PathSegment pathSegment: info.getPathSegments()) {
//                System.out.printf("Path Segment:%s\n", pathSegment.getPath());
//                print("Path Segment Matrix Parameters", pathSegment.getMatrixParameters());
//            }
//         }        
//        
//        if(info.getQueryParameters() != null)
//            print("Query Parameter", info.getPathParameters());
        
        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n", method);
    }
    
    public static void print(String info, MultivaluedMap<String, String> values) {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, List<String>> entry: values.entrySet()) {
            builder.delete(0, builder.length());
            List<String> list = entry.getValue(); int size = list.size();
            for(int i=0; i < size; i++)
                builder.append(list.get(i)).append(i < size -1 ? "," : "");
            
            System.out.printf("[CLIENT] %s: %s:%s\n",info, entry.getKey(), builder.toString());
        }
        
    }
}
