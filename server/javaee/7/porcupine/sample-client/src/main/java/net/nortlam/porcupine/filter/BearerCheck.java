package net.nortlam.porcupine.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.common.Porcupine;
/**
 *
 * @author Mauricio "Maltron" Leal */
@PreMatching
@Provider
@Priority(Porcupine.BEARER_CHECK)
public class BearerCheck extends PorcupineFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(BearerCheck.class.getName());

    @Context
    private ServletContext context;

    public BearerCheck() {
    }
    
    // CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER 
    //  CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER CONTAINER REQUEST FILTER 
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        LOG.log(Level.INFO, ">>> [CLIENT] BearerCheck.filter() START");
        debug(request, "BearerCheck.filter()");
        
        String authenticationHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        if(authenticationHeader == null)
            LOG.log(Level.WARNING, "[CLIENT] filter() NO AUTHENTICATION HEADER WAS FOUND");
        else {
            LOG.log(Level.INFO, "[CLIENT] filter() FOUND Authorization Header:{0}", authenticationHeader);
            // Process the Authentication Header
            String accessToken = parseAccessToken(authenticationHeader);
            if(!isAccessTokenValid(context, request, accessToken))
                redirectErrorPage(context, request);
        }
        
        LOG.log(Level.INFO, ">>> [CLIENT] Bearer.filter() END");
    }
}
