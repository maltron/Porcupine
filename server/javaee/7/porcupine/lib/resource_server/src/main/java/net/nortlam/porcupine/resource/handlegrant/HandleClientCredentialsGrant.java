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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import net.nortlam.porcupine.resource.OAuth2ResourceOperations;
import net.nortlam.porcupine.resource.token.ResourceTokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.grant.ClientCredentials;

/**
 *
 * @author Mauricio "Maltron" Leal */
@ClientCredentials
public class HandleClientCredentialsGrant extends OAuth2ResourceOperations implements HandleGrant {

    private static final Logger LOG = Logger.getLogger(HandleClientCredentialsGrant.class.getName());

    private ResourceTokenManagement tokenManagement;
    private String scope;
    
    public HandleClientCredentialsGrant() {
    }

    public HandleClientCredentialsGrant(ResourceTokenManagement tokenManagement, String scope) {
        this.tokenManagement = tokenManagement;
        this.scope = scope;
    }
    
    // HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    //   HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    @Override
    public void handle(ServletContext context, ContainerRequestContext request) throws IOException {
        LOG.log(Level.INFO, "handle() CLIENT CREDENTIALS SELECTED");
        AccessToken accessToken = new AccessToken();
        try {
            LOG.log(Level.INFO, "handle() Parsing Authorization Header:{0}", request.getHeaderString(HttpHeaders.AUTHORIZATION));
            accessToken.parseAuthorizationBearer(request.getHeaderString(HttpHeaders.AUTHORIZATION));
            
            LOG.log(Level.INFO, "handle() Found an Access Token. Evaluating:{0}", accessToken.getToken());
            accessToken = tokenManagement.retrieveAccessToken(accessToken.getToken());
            
            LOG.log(Level.INFO, "handle() Sucessfull loaded Access Token. Expiration on:{0} NOW:{1} ", 
                    new Object[]{
                    InitParameter.parameterDateFormat(context).format(accessToken.getExpiration()),
                        InitParameter.parameterDateFormat(context).format(new Date())});
            if(accessToken.isExpired(context)) {
                LOG.log(Level.INFO, "handle() Access Token is EXPIRED. Deleting and...");
                tokenManagement.delete(accessToken);

                LOG.log(Level.INFO, "handle() Access Token is EXPIRED. Requesting a new one");
                accessToken = requestAccessToken(Grant.CLIENT_CREDENTIALS, scope, context, request);
                if(accessToken != null) {
                    LOG.log(Level.INFO, "handle() FOUND IT. Storing");
                    tokenManagement.store(accessToken);
                    LOG.log(Level.INFO, "handle() Adding into Header");
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, 
                                            accessToken.toStringAuthorizationBearer());

                // Unable to retrieve a Access Token
                } else redirectNotAuthorize(request);
                
            }
            
            LOG.log(Level.INFO, "handle() END OF CHECKING");
            return;
            
        } catch(IllegalArgumentException ex) {
            
            // No Authorization Bearer found. Request an access Token
            LOG.log(Level.INFO, "handle() Requesting a new Access Token");
            accessToken = requestAccessToken(Grant.CLIENT_CREDENTIALS, scope, context, request);
            if(accessToken != null) {
                LOG.log(Level.INFO, "handle() FOUND IT. Storing");
                tokenManagement.store(accessToken);
                LOG.log(Level.INFO, "handle() Adding into Header");
                request.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, 
                                        accessToken.toStringAuthorizationBearer());
                
            // Unable to retrieve a Access Token
            } else redirectNotAuthorize(request);
            return;
            
        } catch(IOException ex) {
            // Unable to retrieve locally a Access Token
            LOG.log(Level.SEVERE, "This Access Token doesn't exist. IO EXCEPTION:{0}",
                    ex.getMessage());
            redirectNotAuthorize(request);
        }
    }
}
