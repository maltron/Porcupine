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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Default;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import net.nortlam.porcupine.resource.OAuth2ResourceOperations;
import net.nortlam.porcupine.resource.token.ResourceTokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.grant.AuthorizationCode;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Default @AuthorizationCode
public class HandleAuthorizationCodeGrant extends OAuth2ResourceOperations implements HandleGrant {

    private static final Logger LOG = Logger.getLogger(HandleAuthorizationCodeGrant.class.getName());
    
    private ResourceTokenManagement tokenManagement;
    private String scope;
    
    public HandleAuthorizationCodeGrant() {
    }

    public HandleAuthorizationCodeGrant(ResourceTokenManagement tokenManagement, String scope) {
        this.scope = scope;
        this.tokenManagement = tokenManagement;
    }

    // HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    //   HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    @Override
    public void handle(ServletContext context, ContainerRequestContext request) throws IOException {
        LOG.log(Level.INFO, "handle() AUTHORIZATION CODE SELECTED Scope:{0}", scope);
        AccessToken accessToken = null;
        String token = parseAccessToken(request.getHeaderString(HttpHeaders.AUTHORIZATION));
        if(token != null) {
            LOG.log(Level.INFO, "handle() Found an Access Token. Evaluating:{0}", token);
            accessToken = retrieveTokenLocal(context, token);
            if(accessToken != null) {
                
                // Token does exist locally, but it has expired
                LOG.log(Level.INFO, "handle() Access Token Expiration:{0} is Expired ? {1}", 
                        new Object[] {InitParameter.parameterDateFormat(context)
                                            .format(accessToken.getExpiration()),
                        accessToken.isExpired(context)});
                if(accessToken.isExpired(context)) {
                    LOG.log(Level.INFO, "handle() Access Token is EXPIRED. Refreshing");
                    // Proceed with a refresh
                    refreshToken(context, request, accessToken);
                }
                
                // Token does exist and it's still valid
                LOG.log(Level.INFO, "handle() Everything checks all. Token still valid");
                return; // Assuming everything is Ok
                
                
            } else {
                // Token doesn't exist....it means: NOT AUTHORIZE
                LOG.log(Level.SEVERE, "handle() Access Token doesn't exist locally");
                redirectNotAuthorize(request);
                return; 
            }
        }
        
        // Is there any Authorization Code ?
        if(isAuthorizationCode(request)) {
            String authorizationCode = getAuthorizationCode(request);
            LOG.log(Level.INFO, "handle() Found an Authorization Code:{0}", authorizationCode);
            LOG.log(Level.INFO, "handle() Requestin a New Access Token: Grant:AUTHORIZATION_CODE");
            accessToken = requestAccessToken(Grant.AUTHORIZATION_CODE, context, 
                                                            request, authorizationCode);
//                    requestAccessTokenFromAuthorizationCode(context, request, authorizationCode);
            if(accessToken != null) {
                LOG.log(Level.INFO, "handle() Successfull Acquired AccessToken:{0}", 
                                                accessToken.getToken());
                // Now trying to save the accessToken locally for future requests
                storeTokenLocal(accessToken);
                // Everything seems fine
                return;
            }
        }
        
        // No Authorization Code from this point forward
        // Neither there is an Access Token either
        // Conclusion: Redirect to Authorization Server 
        LOG.log(Level.INFO, "handle() Redirecting to Authorization Server");
        redirectAuthorizationServer(context, request, Grant.AUTHORIZATION_CODE, scope);
    }
    
    private void storeTokenLocal(AccessToken accessToken) {
        try {
            tokenManagement.store(accessToken);
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "storeTokenLocal() Unable to store AccessToken:{0}",
                    ex.getMessage());
        }
    }
    
    private AccessToken retrieveTokenLocal(ServletContext context, String token) {
        AccessToken accessToken = null;
        try {
            accessToken = tokenManagement.retrieveAccessToken(token);
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "retrieveTokenLocal() Problems to retrieve token locally:{0}",
                    ex.getMessage());
        }
        
        return accessToken;
    }
    
    private void refreshToken(ServletContext context, ContainerRequestContext request, 
            AccessToken expiredAccessToken) {
        LOG.log(Level.INFO, ">>> [CLIENT] refreshToken() ");
        // First, request a new Access Token to the server
        AccessToken newAccessToken = requestAccessToken(Grant.REFRESH_TOKEN, scope, context, request);
//                refreshAccessToken(context, request, expiredAccessToken);
        if(newAccessToken == null) {
            // Something wrong went wrong
            LOG.log(Level.WARNING, "refreshToken() Unable to refresh an Access Token");
            redirectErrorPage(context, request, "Unable to refresh an Access Token");
        } else { 
            try {
                // Good. Delete the old Access Token
                tokenManagement.delete(expiredAccessToken);
                // Save it the new one
                tokenManagement.store(newAccessToken);
            } catch(IOException ex) {
                LOG.log(Level.SEVERE, "refreshToken() IO EXCEPTION:{0}", ex.getMessage());
                redirectErrorPage(context, request, "Unable to store expired/new Access Token");
            }
        }
    }
}
