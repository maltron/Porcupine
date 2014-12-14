package net.nortlam.porcupine.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

/**
 * Talks to Authorization Server and guarantees the access_token is valid
 *
 * @author Mauricio "Maltron" Leal */
public class PorcupineFilter implements ContainerRequestFilter {
    
    private static final Logger LOG = Logger.getLogger(PorcupineFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        for(Map.Entry<String, List<String>> entry: context.getUriInfo().getQueryParameters().entrySet()) {
            for(String value: entry.getValue())
                if(entry.getKey())
        }
        
    }
}
