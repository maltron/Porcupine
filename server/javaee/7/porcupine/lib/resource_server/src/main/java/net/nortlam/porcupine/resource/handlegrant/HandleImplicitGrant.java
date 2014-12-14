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
package net.nortlam.porcupine.resource.handlegrant;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import net.nortlam.porcupine.resource.OAuth2ResourceOperations;
import net.nortlam.porcupine.resource.token.ClientTokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.grant.Implicit;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Implicit
public class HandleImplicitGrant extends OAuth2ResourceOperations implements HandleGrant {

    private static final Logger LOG = Logger.getLogger(HandleImplicitGrant.class.getName());

    private ClientTokenManagement tokenManagement;
    private String scope;
    
    public HandleImplicitGrant() {
    }
    
    public HandleImplicitGrant(ClientTokenManagement tokenManagement, String scope) {
        this.tokenManagement = tokenManagement;
        this.scope = scope;
    }

    // HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    //   HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    @Override
    public void handle(ServletContext context, ContainerRequestContext request) throws IOException {
        LOG.log(Level.INFO, "handle() IMPLICIT SELECTED");
        AccessToken accessToken = null;
        String token = parseAccessToken(request.getHeaderString(HttpHeaders.AUTHORIZATION));
        if(token != null) {
            LOG.log(Level.INFO, "handle() Found an Access Token. Evaluating:{0}", token);
            
            try {
                accessToken = tokenManagement.retrieveAccessToken(token);
                LOG.log(Level.INFO, "handle() Sucessfull loaded Access Token. Expiration on:{0}", 
                        InitParameter.parameterDateFormat(context).format(accessToken.getExpiration()));
                if(accessToken.isExpired(context)) {
                    LOG.log(Level.INFO, "handle() Access Token is EXPIRED. Deleting and...");
                    tokenManagement.delete(accessToken);
                    LOG.log(Level.INFO, "handle() Access Token is EXPIRED. Requesting a new one");
                    redirectAuthorizationServer(context, request, Grant.IMPLICIT, scope);
                    return;
                }
                
                // Token does exist and it still valid
                LOG.log(Level.INFO, "handle() Everything checks all. Token still valid");
                return;
                
            } catch(IOException ex) {
                LOG.log(Level.SEVERE, "handle() (Assuming Access Token doesn't exist) IO EXCEPTION:{0}", ex.getMessage());
                // Assuming Access Token doesn't exist
                redirectNotAuthorize(request);
                return;
            }
        }
        
        String location = request.getHeaderString(HttpHeaders.LOCATION);
        LOG.log(Level.INFO, "handle() location:{0}", location);
        
        MultivaluedMap<String, String> parameters = request.getUriInfo().getQueryParameters();
        if(parameters.isEmpty()) {
            LOG.log(Level.INFO, "handle() NO PARAMETERS. Redirecting to Authorization Server");
            redirectAuthorizationServer(context, request, Grant.IMPLICIT, scope);
            return;
        }
        
        token = fetchParameter(OAuth2.PARAMETER_ACCESS_TOKEN, parameters);
        String tokenType = fetchParameter(OAuth2.PARAMETER_TOKEN_TYPE, parameters);
        String expirationInSeconds = fetchParameter(OAuth2.PARAMETER_EXPIRES_IN, parameters);
        String state = fetchParameter(OAuth2.PARAMETER_STATE, parameters);
        if(token != null && tokenType != null && expirationInSeconds != null) {
            LOG.log(Level.INFO, "handle() Processing a genuine response from Authorization Server");
            accessToken = new AccessToken();
            accessToken.setToken(token);
            accessToken.setTokenType(tokenType);
            long value = Long.valueOf(expirationInSeconds)*1000; // Turn back into Milliseconds
            accessToken.setExpiration(new Date(value));
            
            // Store it
            try {
                LOG.log(Level.INFO, "handle() Storing Access Token");
                tokenManagement.store(accessToken);
            } catch(IOException ex) {
                LOG.log(Level.SEVERE, "handle() IO EXCEPTION:{0}", ex.getMessage());
                redirectErrorPage(context, request, "Unable to store Access Token");
                return;
            }
            
            LOG.log(Level.INFO, "handle() Everything checks all. Token is used for the first time");
            return;
            
        } 
        
        // From this point forward, assuming some parameters wasn't right
        // Conclusion: Redirect to Authorization Server
        redirectAuthorizationServer(context, request, Grant.IMPLICIT, scope);
    }
}
