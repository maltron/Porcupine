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
package net.nortlam.porcupine.server.handlegrant;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.entity.Scope;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.exception.UnauthorizedClientException;
import net.nortlam.porcupine.common.exception.UnsupportedResponseTypeException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.grant.ResourceOwnerPasswordCredentials;

/**
 *
 * @author Mauricio "Maltron" Leal */
@ResourceOwnerPasswordCredentials
public class HandleGrantResourceOwnerPasswordCredentials implements HandleGrant {

    private static final Logger LOG = Logger.getLogger(HandleGrantResourceOwnerPasswordCredentials.class.getName());

    private HandleGrantConfiguration config;

    public HandleGrantResourceOwnerPasswordCredentials() {
    }

    public HandleGrantResourceOwnerPasswordCredentials(HandleGrantConfiguration config) {
        this.config = config;
    }

    // HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    //  HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    @Override
    public AccessToken generateToken() throws InvalidRequestException, 
            InvalidScopeException, ServerErrorException, UnauthorizedClientException,
                        AccessDeniedException, UnsupportedResponseTypeException {
            OAuth2 oauth = config.getOAuth();
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() RESOURCE OWNER PASSWORD GRANT SELECTED");
            LOG.log(Level.INFO, ">>> [SERVER] GrantType:{0}, Username:{1}, Password:{2},"
                    + " Scope:{3}", new Object[] {oauth.getGrantType(), 
                        oauth.getUsername(),
                        oauth.getPassword(), oauth.getScope()});
            
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() Searching Scope based on "+
                    " Username:{0} and Password:{1}", new Object[] {
                                        oauth.getUsername(), 
                                            oauth.getPassword()});
            Scope scope = config.getClientService().findScopeByUsernamePassword(
                    oauth.getUsername(), oauth.getPassword());
            if(scope == null) { // Didn't find it
                oauth.setErrorMessage("### HandleGrantResourceOwnerPasswordCredentials.generateToken() Unable to find"+
                        " scope based on Username and Password");
                throw new InvalidScopeException(oauth);
            }
            
            // Is this scope enable for this particular grant ?
            LOG.log(Level.INFO, ">>> [SERVER] This Grant {0} is allowed ?", 
                                        Grant.RESOURCE_OWNER_PASSWORD_CREDENTIALS);
            if(!oauth.isGrantAllowed(scope, Grant.RESOURCE_OWNER_PASSWORD_CREDENTIALS)) {
                oauth.setErrorMessage("### HandleGrantResourceOwnerPasswordCredentials.generateToken() "+
                        "Grant is not allowed for this particular Scope");
                throw new UnsupportedResponseTypeException(oauth);
            }
            
            LOG.log(Level.INFO, ">>> [SERVER] Everything checks out. Generating Access Token");
            AccessToken accessToken = new AccessToken(config.getContext(), scope);
            // No need for a refresh Token for this Grant
            accessToken.setRefreshToken(null);
            config.getTokenManagement().storeAccessToken(oauth, accessToken);
            
            return accessToken;
    }
}
