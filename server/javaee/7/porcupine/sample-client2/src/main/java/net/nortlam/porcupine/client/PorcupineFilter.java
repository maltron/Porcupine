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

import net.nortlam.porcupine.client.handlegrant.HandleGrant;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import net.nortlam.porcupine.common.OAuth2;

/**
 * Filter used to talk to Authorization Server and obtain an Access Token
 * in order to get access to the content
 *
 * @author Mauricio "Maltron" Leal */
public class PorcupineFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(PorcupineFilter.class.getName());
    
    private ServletContext context;
    private HandleGrant handleGrant;

    public PorcupineFilter(ServletContext context, HandleGrant handleGrant) {
        this.context = context;
        this.handleGrant = handleGrant;
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        System.out.printf(">>> PORCUPINE filter() START START START START >>>\n");
        OAuth2ClientOperations.debug(request, "Porcupine.filter()");
        MultivaluedMap<String, String> parameters = request.getUriInfo().getQueryParameters();
        
        List<String> error = parameters.get(OAuth2.PARAMETER_ERROR);
        List<String> error_description = parameters.get(OAuth2.PARAMETER_ERROR_DESCRIPTION);
        if(error != null && error_description != null) {
            LOG.log(Level.WARNING, "filter() An error was detected. Redirecting to Error Page");
            OAuth2ClientOperations.redirectErrorPage(context, request);
            return;
        }

        // Handles the Grant, depending on what was specified on
        // the Secure Annotation
        handleGrant.handle(context, request);
        
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
}
