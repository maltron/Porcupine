package net.nortlam.porcupine.filter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.Porcupine;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 *
 * @author Mauricio "Maltron" Leal */
@PreMatching
@Provider
@Priority(Porcupine.AUTHORIZATION_CODE)
public class AuthorizationCodeCheck extends PorcupineFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(AuthorizationCodeCheck.class.getName());

    @Context
    private ServletContext context;
    
    // CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER 
    //  CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER 
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        LOG.log(Level.INFO, ">>> [CLIENT] AuthorizationCodeCheck.filter() START");
        MultivaluedMap<String, String> parameters = request.getUriInfo().getQueryParameters();
        List<String> codes = parameters.get(OAuth2.PARAMETER_CODE);
        if(codes != null) {
            if(codes != null && codes.size() > 1)
                LOG.log(Level.WARNING, "[CLIENT] filter() More than one <code> has found");
            
            // Processing this code
            String authorizationCode = codes.get(0);
            LOG.log(Level.INFO, "[CLIENT] filter() FOUND Code:{0}. Requesting an Access Token", authorizationCode);
            
            AccessToken accessToken = requestAccessTokenFromAuthorizationCode(
                            context, request, authorizationCode);
            if(accessToken != null) {
                // Try to remove the Query <code>....It is no longer necessary from this point 
                request.getUriInfo().getQueryParameters().remove(OAuth2.PARAMETER_CODE);

                // SUCCESS: Acquired a an Access Token
                LOG.log(Level.INFO, "[CLIENT] filter() Successfull obtained a Acccess Token:{0}", accessToken != null ? accessToken.getToken() : "NULL");
                // Add the Token into Header (hopefully, it will be read in the next step)
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, 
                        String.format("Bearer %s", accessToken.getToken()));

            // ERROR: Unable to retrive an Access Token from Authorization Server
            } else LOG.log(Level.SEVERE, "AuthorizationCodeCheck.filter() Unable to retrieve an Access Token from Authorization Server");
                
        }
        LOG.log(Level.INFO, ">>> [CLIENT] AuthorizationCodeCheck.filter() END");
    }
}
