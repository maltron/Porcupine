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
package net.nortlam.porcupine.client.handlegrant;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import net.nortlam.porcupine.client.OAuth2ClientOperations;
import net.nortlam.porcupine.client.token.ClientTokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.grant.ResourceOwnerPasswordCredentials;

/**
 *
 * @author Mauricio "Maltron" Leal */
@ResourceOwnerPasswordCredentials
public class HandleResourceOwnerPasswordCredentialsGrant extends OAuth2ClientOperations implements HandleGrant {

    private static final Logger LOG = Logger.getLogger(HandleResourceOwnerPasswordCredentialsGrant.class.getName());

    private ClientTokenManagement tokenManagement;
    private String username;
    private String password;
    private String scope;
    
    public HandleResourceOwnerPasswordCredentialsGrant() {
    }

    public HandleResourceOwnerPasswordCredentialsGrant(ClientTokenManagement tokenManagement, 
                                            String username, String password, String scope) {
        this.tokenManagement = tokenManagement;
        this.username = username; this.password = password;
        this.scope = scope;
    }

    // HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    //   HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    @Override
    public void handle(ServletContext context, ContainerRequestContext request) throws IOException {
        LOG.log(Level.INFO, "handle() RESOURCE OWNER PASSWORD CREDENTIAL SELECTED");
        AccessToken accessToken = null;
        String token = parseAccessToken(request.getHeaderString(HttpHeaders.AUTHORIZATION));
        if(token != null) {
            LOG.log(Level.INFO, "handle() Found an Access Token. Evaluating:{0}", token);
            
            try {
                accessToken = tokenManagement.retrieveAccessToken(token);
                LOG.log(Level.INFO, "handle() Sucessfull loaded Access Token. Expiration on:{0} NOW:{1} ", 
                        new Object[]{
                        InitParameter.parameterDateFormat(context).format(accessToken.getExpiration()),
                            InitParameter.parameterDateFormat(context).format(new Date())});
                if(accessToken.isExpired(context)) {
                    LOG.log(Level.INFO, "handle() Access Token is EXPIRED. Deleting and...");
                    tokenManagement.delete(accessToken);
                    
                    LOG.log(Level.INFO, "handle() Access Token is EXPIRED. Requesting a new one");
                    accessToken = requestAccessToken(Grant.RESOURCE_OWNER_PASSWORD_CREDENTIALS,
                            scope, username, password, context, request, null, null);
                    if(accessToken != null) {
                        LOG.log(Level.INFO, "handle() Successfull acquired. Storing it");
                        tokenManagement.store(accessToken);
                        addAuthorizationHeader(request, accessToken);
                        
                    // If something wrong happen, then it's not authorized to used
                    }  else {
                        redirectNotAuthorize(request);
                        return;
                    }
                }
                
                // Token does exist and it still valid
                LOG.log(Level.INFO, "handle() END OF CHECKING");
                return;
                
            } catch(IOException ex) {
                LOG.log(Level.SEVERE, "handle() (Assuming Access Token doesn't exist) IO EXCEPTION:{0}", ex.getMessage());
                // Assuming Access Token doesn't exist
                redirectNotAuthorize(request);
                return;
            }
        }
        
        // From this point forward, there isn't any Authorization bearer
        // or some parameters wasn't right. Conclusion: Ask the Authorization Server for 
        // an Access Token
        LOG.log(Level.INFO, "handle() No Authoriation found, requesting an Access Token");
        accessToken = requestAccessToken(Grant.RESOURCE_OWNER_PASSWORD_CREDENTIALS, 
                scope, username, password, context, request, null, null);
        // If something goes bad, a redirect page will be presented
        if(accessToken != null) {
            LOG.log(Level.INFO, "handle() AccessToken accepted");
            try {
                LOG.log(Level.INFO, "handle() Storing Access Token");
                tokenManagement.store(accessToken);
                // All is well, presenting the Authorization Bearer
                LOG.log(Level.INFO, "handle() Adding Token into Header AUTHORIZATION");
//                request.getHeaders().put(HttpHeaders.AUTHORIZATION, accessToken.toListAuthorizationBearer());
                addAuthorizationHeader(request, accessToken);
                
            } catch(IOException ex) {
                LOG.log(Level.SEVERE, "Unable to store Access Token locally."+
                        "IO EXCEPTION:{0}", ex.getMessage());
                redirectNotAuthorize(request);
            }
        // If something wrong happen, then it's not authorized to used
        } else redirectNotAuthorize(request);
    }
}
