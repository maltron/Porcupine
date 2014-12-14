package net.nortlam.porcupine.filter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.Porcupine;

/**
 *
 * @author Mauricio "Maltron" Leal */
@PreMatching
@Provider
@Priority(Porcupine.OAUTH2_CHECK)
public class OAuth2Check extends PorcupineFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(OAuth2Check.class.getName());
    
    @Context
    private ServletContext context;

    public OAuth2Check() {
    }
    
    // CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER 
    //  CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER 
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        LOG.log(Level.INFO, ">>> [CLIENT] OAuth2.filter() START");
        // Header with a valid Token
        String authenticationHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        // Any Parameters such as: 
        MultivaluedMap<String, String> parameters = request.getUriInfo().getQueryParameters();
        List<String> code = parameters.get(OAuth2.PARAMETER_CODE);
        List<String> error = parameters.get(OAuth2.PARAMETER_ERROR);
        
        // If all information is Null, then redirect to the authorizer
        if(authenticationHeader == null && code == null && error == null) {
            LOG.log(Level.WARNING, "[CLIENT] filter() Nothing has found. Redirect to Authorizer");
            redirectAuthorizationServer(context, request);
            
        } else if(error != null) {
            // FOUND AN ERROR: Redirecting to Error Page
            redirectErrorPage(context, request);
        }
        // In case there is some <code>, then it will be handle by another filter
        // AuthorizationCodeCheck
        
        LOG.log(Level.INFO, ">>> [CLIENT] Oauth2.filter() END");
    }
}
