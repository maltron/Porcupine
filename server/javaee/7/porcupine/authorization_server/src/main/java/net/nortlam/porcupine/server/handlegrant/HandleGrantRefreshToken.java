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
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.exception.UnauthorizedClientException;
import net.nortlam.porcupine.common.exception.UnsupportedResponseTypeException;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class HandleGrantRefreshToken implements HandleGrant {

    private static final Logger LOG = Logger.getLogger(HandleGrantRefreshToken.class.getName());

    private HandleGrantConfiguration config;

    public HandleGrantRefreshToken() {
    }

    public HandleGrantRefreshToken(HandleGrantConfiguration config) {
        this.config = config;
    }
    
    // HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    //  HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    @Override
    public AccessToken generateToken() throws InvalidRequestException, 
            InvalidScopeException, ServerErrorException, UnauthorizedClientException,
                        AccessDeniedException, UnsupportedResponseTypeException {
            LOG.log(Level.INFO, ">>> [SERVER] getToken() REFRESH TOKEN");
            
            LOG.log(Level.INFO, ">>> [SERVER] getToken() Looking for an Access Token based on this Refresh Token");
            AccessToken oldAccessToken = config.getTokenManagement().retrieveAccessTokenFromRefreshToken(
                                                        config.getOAuth(), config.getOAuth().getRefreshToken());
            if(oldAccessToken == null) { // Didn't find it ??? Then there is something odd
                config.getOAuth().setErrorMessage(("### TokenEndpoint.getToken() Unable to find Refresh Token"));
                LOG.log(Level.WARNING, ">>> [SERVER] getToken() Unable to find "+
                        "Refresh Token:{0}", config.getOAuth().getRefreshToken());
                throw new AccessDeniedException(config.getOAuth());
            }
            
            LOG.log(Level.INFO, ">>> [SERVER] getToken() Found the Access Token "+
                    "and it should be expired. Is Expired ? {0}", 
             oldAccessToken.isExpired(config.getContext()));
            if(!oldAccessToken.isExpired(config.getContext())) {
                config.getOAuth().setErrorMessage("### TokenEndpoint.getToken() Access Token must be expired in order to refresh it");
                LOG.log(Level.WARNING, ">>> [SERVER] getToken() Access Token "+
                        " must be expired. Need to investigate.");
                throw new AccessDeniedException(config.getOAuth());
            }
            
//            // Check if the scope match with the one on the Access Token
//            if(!oldAccessToken.getTokenType().equals(oauth.getScope())) {
//                oauth.setErrorMessage("### TokenEndpoint.getToken() Scope "+
//                        " doesn't match with the previous one");
//                LOG.log(Level.WARNING, "### [SERVER] getToken() Scope:{0}"+
//                        " doesn't match with the one on the Access Token:{1}",
//                        new Object[] {oauth.getScope(), accessToken.getTokenType()});
//                throw new AccessDeniedException(oauth);
//            }
            
            // Look for the Scope 
            LOG.log(Level.INFO, ">>> [SERVER] Everything seems in order. "+
                    "Refreshing with a new Access Token (and deleting the old one)");
            AccessToken accessToken = new AccessToken(config.getContext(), oldAccessToken, config.getPrincipal());
            config.getTokenManagement().refreshingAccessToken(config.getOAuth(), oldAccessToken, accessToken);
            
            return accessToken;
    }

}
