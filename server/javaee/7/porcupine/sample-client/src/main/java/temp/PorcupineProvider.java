package temp;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.util.URIBuilder;

/**
 *
 * @author Mauricio "Maltron" Leal */
//@Provider
public class PorcupineProvider {
//implements ContainerRequestFilter {
//
//    private static final Logger LOG = Logger.getLogger(PorcupineProvider.class.getName());
//    
//    // Ideally, this should be state 
//    public static final Grant PORCUPINE_GRANT = Grant.AUTHORIZATION_CODE;
//    public static final String PORCUPINE_AUTHORIZATION_SERVER_SCHEME = "http";
//    public static final String PORCUPINE_AUTHORIZATION_SERVER_HOST = "localhost";
//    public static final int PORCUPINE_AUTHORIZATION_SERVER_PORT = 8080;
//    public static final String PORCUPINE_AUTHORIZATION_ENDPOINT = "/server/oauth2/authorize";
//    public static final String PORCUPINE_AUTHORIZATION_TOKEN_ENDPOINT = "/server/oauth2/token";
//    public static final String PORCUPINE_AUTHORIZATION_CHECK_ENDPOINT = "/server/oauth2/check";
//    public static final String PORCUPINE_CLIENT_ID = "cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=";
//    public static final String PORCUPINE_CLIENT_SECRET = "cG9yY3VwaW5lLWNsaWVudC1zZWNyZXQtYjRmM2QyZmYtYmUxZS00ZjkxLWFiZWQtMjAyYWU1MTRhMDgy";
//    public static final String PORCUPINE_SCOPE = "EMAIL";
//    // THIS INFORMATION SHOULD BE ASKED OR LEAVE IN A CONFIG/SETUP FILE
//    public static final String SAMPLE_USERNAME = "maltron@gmail.com";
//    public static final String SAMPLE_PASSWORD = "maltron";
//    public static final String PORCUPINE_ERROR_PAGE = "http://localhost:8080/client/error";
//
//    @Override
//    public void filter(ContainerRequestContext request) throws IOException {
//        LOG.log(Level.INFO, ">>> PorcupineProvider.filter() START");
//        debug(request.getUriInfo());
//        debugHeaders(request);
//        
//        MultivaluedMap<String, String> parameters = request.getUriInfo().getQueryParameters();
//        String accessToken = null;
//        
//        // Check if there is any Header with Bearer for Authorization
//        // Before going any further, try to detect some Authentication Header
//        LOG.log(Level.INFO, ">>> PorcupineProvider.filter() Checking for Authorization Header");
//        String authenticationHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);
//        if(authenticationHeader == null) {
//            LOG.log(Level.SEVERE, "### PorcupineProvider.filter() Missing Header <Authorization> with a Bearer");
//        } else {
//            LOG.log(Level.INFO, ">>> PorcupineProvider.filter() Founda a Authorization Header. Processing");
//            // Get the Token from the authorization 
//            accessToken = parseAccessToken(authenticationHeader);
//            // if it's not valid, then error
//            try {
//                if(!isAccessTokenValid(accessToken)) {
//                    LOG.log(Level.SEVERE, "### PorcupineProvider.filter() Access Token has being expired or it's not valid");
//                    showError(request, "access_denied", "Access Token has being expired or it's not valid");
//                    return;
//                }
//            } catch(AccessDeniedException ex) {
//                LOG.log(Level.SEVERE, "### PorcupineProvider.filter() Unable to reach the checkendpoint");
//                showError(request, "invalid_request,", "Unable to reach the checkendpoint");
//                return;
//            }
//            LOG.log(Level.INFO, ">>> PorcupineProvider.filter() SUCCESS !!!!");
//            return;
//        }
//            
//        LOG.log(Level.INFO, ">>> PorcupineProvider.filter() Proceding to evaluate other parameters");
//        
//        String code = null;
//        // Error
//        String error = null; 
//        String error_description = null; 
//        String error_uri = null;
//        
//        
//        // There is no parameters ?
//        if(parameters.isEmpty()) {
//            // Redirect to the Authorization Server
//            LOG.log(Level.INFO, ">>> PorcupineProvider.filter() NO PARAMETERS. Calling Authorizer");
//            request.abortWith(Response.seeOther(requestAuthorizationCode(request.getUriInfo())).build());
//            return;
//        }
//        
//        // Try to identify which parameter should be used
//        for(Map.Entry<String, List<String>> entry: parameters.entrySet()) 
//            for(String value: entry.getValue()) {
//                LOG.log(Level.INFO, ">>> PorcupineProvider.filter() <iteration> {0}:{1}", 
//                                                new Object[] {entry.getKey(), value});
//                
//                // Access Token ?
//                if(entry.getKey().equals(OAuth2.PARAMETER_ACCESS_TOKEN)) {
//                    LOG.log(Level.INFO, ">>> PorcupineProvider.filter() [FOUND] access_token:{0}", value);
//                    accessToken = value; continue;
//                }
//                
//                // Code ?
//                if(entry.getKey().equals(OAuth2.PARAMETER_CODE)) {
//                    LOG.log(Level.INFO, ">>> PorcupineProvider.filter() [FOUND] code:{0}", value);
//                    code = value; continue;
//                }
//                
//                // Error ?
//                if(entry.getKey().equals(OAuth2.PARAMETER_ERROR)) {
//                    LOG.log(Level.INFO, ">>> PorcupineProvider.filter() [FOUND] error:{0}", value);
//                    error = value; continue;
//                }
//                
//                // Error Description ?
//                if(entry.getKey().equals(OAuth2.PARAMETER_ERROR_DESCRIPTION)) {
//                    LOG.log(Level.INFO, ">>> PorcupineProvider.filter() [FOUND] error_description:{0}", value);
//                    error_description = value; continue;
//                }
//                
//                // Error URI ?
//                if(entry.getKey().equals(OAuth2.PARAMETER_ERROR_URI)) {
//                    LOG.log(Level.INFO, ">>> PorcupineProvider.filter() [FOUND] error_uri:{0}", value);
//                    error_uri = value; continue;
//                }
//            }
//        
//        if(code != null) {
//            LOG.log(Level.INFO, ">>> PorcupineProvider.filter() Processing Code:{0}", code);
//            try {
//                // Does it have a code ? Then it should ask for a access_token
//                LOG.log(Level.INFO, ">>> PorcupineProvider.filter() Retrieving Access Token from TokenEndpoint");
//                AccessToken token = requestAccessTokenFromAuthorizationCode(
//                                                    request.getUriInfo(), code);
//                LOG.log(Level.INFO, ">>> PorcupineProvider.filter() Access Token Acquired:{0}", token.getToken());
//                LOG.log(Level.INFO, ">>> PorcupineProvider.filter() Redirect using a Authorization Bearer");
//                // Request the same address, using this AccessToken
//                request.abortWith(Response.seeOther(request.getUriInfo().getAbsolutePath())
//                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token.getToken())).build());
//                return; 
//                
//            } catch(AccessDeniedException ex) {
//                LOG.log(Level.SEVERE, "### PorcupineProvider.filter() ACCESS DENIED:{0}", ex.getMessage());
//                request.abortWith(Response.seeOther(URIBuilder.buildURI(PORCUPINE_ERROR_PAGE)).build());
//            }
//            
//        // ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR 
//        } else if(error != null) {
//            URI uriError = URIBuilder.buildURI(PORCUPINE_ERROR_PAGE, 
//                    OAuth2.PARAMETER_ERROR, error,
//                    // If the value is null, it won't show 
//                    OAuth2.PARAMETER_ERROR_DESCRIPTION, error_description);
//            request.abortWith(Response.seeOther(uriError).build());
//        }
//        
//        LOG.log(Level.INFO, ">>> PorcupineProvider.filter() END");
//    }
//    
//    private URI requestAuthorizationCode(UriInfo info) {
//    //      http://localhost:8080/server/oauth2/authorize?
//    //      response_type=code
//    //      &client_id=cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=
//    //      &scope=EMAIL
//    //      &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fclient
//        
//        return URIBuilder.buildURI(// Authorization Server URL
//                PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_ENDPOINT, 
//                // Parameters
//                OAuth2.PARAMETER_RESPONSE_TYPE, OAuth2.PARAMETER_RESPONSE_TYPE_CODE,
//                OAuth2.PARAMETER_CLIENT_ID, PORCUPINE_CLIENT_ID,
//                OAuth2.PARAMETER_SCOPE, PORCUPINE_SCOPE,
//                OAuth2.PARAMETER_REDIRECT_URI, info.getAbsolutePath().toASCIIString());
//    }
//    
//    private AccessToken requestAccessTokenFromAuthorizationCode(UriInfo info, String code) 
//                                                    throws AccessDeniedException {
//        AccessToken accessToken = null;
//        
//        URI tokenendpoint = URIBuilder.buildURI(PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_TOKEN_ENDPOINT);
//        
//        Client client = ClientBuilder.newClient()
//                // PENDING: It should be asked what kind of authentication
//                .register(new FormAuthenticator(tokenendpoint,
//                                            SAMPLE_USERNAME, SAMPLE_PASSWORD));
//        WebTarget targetTokenendpoint = client.target(tokenendpoint);
//
//        // Required Parameters in case of Authorization Grant Code (Step #2)
//        Form form = new Form();
//        form.param(OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE);
//        form.param(OAuth2.PARAMETER_CODE, code);
//        form.param(OAuth2.PARAMETER_REDIRECT_URI, info.getAbsolutePath().toASCIIString());
//        form.param(OAuth2.PARAMETER_CLIENT_ID, PORCUPINE_CLIENT_ID);
//        
//        Response response = null;
//        try {
//            response = targetTokenendpoint.request(MediaType.APPLICATION_XML)
//                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
//            if(response.getStatus() == Response.Status.OK.getStatusCode()) {
//                accessToken = response.readEntity(AccessToken.class);
//            }
//            
//        } finally {
//            if(response != null) response.close();
//        }
//        
//        return accessToken;
//    }
//    
//    private String parseAccessToken(String authenticationHeader) {
//        if(authenticationHeader == null) return null;
//        
//        return authenticationHeader.substring("Bearer ".length(), authenticationHeader.length());
//    }
//    
//    private boolean isAccessTokenValid(String accessToken) throws AccessDeniedException {
//        Client client = ClientBuilder.newClient()
//                .register(new FormAuthenticator(checkEndpoint(), SAMPLE_USERNAME, SAMPLE_PASSWORD));
//        WebTarget checkEndpoint = client.target(checkEndpoint())
//                    .queryParam(OAuth2.PARAMETER_ACCESS_TOKEN, accessToken);
//        Response response = checkEndpoint.request(MediaType.APPLICATION_JSON).get();
//        
//        // Anything different from Ok, it means something went wrong
//        return response.getStatus() == Response.Status.OK.getStatusCode();
//    }
//    
//    private URI authorizeEndpoint() {
//        return URIBuilder.buildURI(PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_ENDPOINT);
//    }
//    
//    private URI tokenEndpoint() {
//        return URIBuilder.buildURI(PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_TOKEN_ENDPOINT);
//    }
//    
//    private URI checkEndpoint() {
//        return URIBuilder.buildURI(PORCUPINE_AUTHORIZATION_SERVER_SCHEME,
//                PORCUPINE_AUTHORIZATION_SERVER_HOST,
//                PORCUPINE_AUTHORIZATION_SERVER_PORT,
//                PORCUPINE_AUTHORIZATION_CHECK_ENDPOINT);
//    }
//    
//    private URI errorPage(String error, String errorDescription) {
//        return URIBuilder.buildURI(PORCUPINE_ERROR_PAGE,
//                OAuth2.PARAMETER_ERROR, error,
//                OAuth2.PARAMETER_ERROR_DESCRIPTION, errorDescription);
//    }
//    
////    private String getAccessToken(UriInfo info, String code) {
////    //     POST /token HTTP/1.1
////    //     Host: server.example.com
////    //     Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
////    //     Content-Type: application/x-www-form-urlencoded
////    //
////    //     grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA
////    //     &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
////        
////        Client client = ClientBuilder.newClient();
////        WebTarget target = client.target()
////        
////        String body = URIBuilder.buildParameters(
////                OAuth2.PARAMETER_GRANT_TYPE, OAuth2.PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE,
////                OAuth2.PARAMETER_CODE, code,
////                OAuth2.PARAMETER_REDIRECT_URI, info.getAbsolutePath().toASCIIString());
////    }
//    
//    private void showError(ContainerRequestContext request, String error, String errorDescription) {
//        request.abortWith(Response.seeOther(errorPage(error, errorDescription)).build());
//    }
//    
//    
//    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
//    //   DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
//    
//    private void debugHeaders(ContainerRequestContext request) {
//        for(Map.Entry<String, List<String>> entry: request.getHeaders().entrySet()) {
//            String key = entry.getKey();
//            for(String value: entry.getValue())
//                LOG.log(Level.INFO, "*** Provider.debugHeaders() Header:{0} = {1}", 
//                        new Object[] {key, value});
//        }
//    }
//    
//    private void debug(UriInfo info) {
//        LOG.log(Level.INFO, "*** PorcupineProvider.debug() Absolute Path:{0}", info.getAbsolutePath() != null ? info.getAbsolutePath().toString() : "NULL");
//        LOG.log(Level.INFO, "*** PorcupineProvider.debug() Base URI:{0}", info.getBaseUri() != null ? info.getBaseUri().toString() : "NULL");
//        LOG.log(Level.INFO, "*** PorcupineProvider.debug() Path:{0}", info.getPath() != null ? info.getPath().toString() : "NULL");
//        LOG.log(Level.INFO, "*** PorcupineProvider.debug() Request URI:{0}", info.getRequestUri() != null ? info.getRequestUri().toString() : "NULL");
//    }
}
