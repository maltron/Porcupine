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
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.exception.UnauthorizedClientException;
import net.nortlam.porcupine.common.exception.UnsupportedResponseTypeException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.grant.AuthorizationCode;

/**
 *
 * @author Mauricio "Maltron" Leal */
@AuthorizationCode
public class HandleGrantAuthorizationCode implements HandleGrant {

    private static final Logger LOG = Logger.getLogger(HandleGrantAuthorizationCode.class.getName());

    private HandleGrantConfiguration config;
    
    public HandleGrantAuthorizationCode() {
    }
    
    public HandleGrantAuthorizationCode(HandleGrantConfiguration config) {
        this.config = config;
    }

    // HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    //  HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT HANDLE GRANT 
    @Override
    public AccessToken generateToken() throws InvalidRequestException, 
            InvalidScopeException, ServerErrorException, UnauthorizedClientException,
                        AccessDeniedException, UnsupportedResponseTypeException {
            OAuth2 oauth = config.getOAuth();
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() AUTHORIZATION CODE GRANT SELECTED");
            
            // Try to retrieve the code from Queue (it might be expired)
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() Looking for an "+
                    "existence of a Code:{0}", oauth.getCode());
//            AuthorizationCode authorizationCode = messaging.restoreCode(oauth, oauth.getCode());
            net.nortlam.porcupine.common.token.AuthorizationCode authorizationCode = 
                    config.getTokenManagement().restoreCode(oauth, 
                                                        oauth.getCode());
            if(authorizationCode == null) {
                oauth.setErrorMessage("### HandleGrantAuthorizationCode.generateToken() Unable to locate this code from queue. Either was expired or it doesn't exist");
                // Unable to find it this code or the code has expired
                throw new AccessDeniedException(oauth);
            }
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() CODE has being FOUND");
            
            // Check if the Authorization code is not expired
            if(authorizationCode.isExpired(config.getContext())) {
                LOG.log(Level.INFO, ">>> [SERVER] Authorization Code is EXPIRED. Deleting.");
                config.getTokenManagement().deleteCode(oauth, authorizationCode);
                
                oauth.setErrorMessage("### HandleGrantAuthorizationCode.generateToken() Authorization Code is expired");
                // Unable to find it this code or the code has expired
                throw new AccessDeniedException(oauth);
            }
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() Code is STILL VALID. Expires on {0}",
                    InitParameter.parameterDateFormat(config.getContext()).format(
                                                authorizationCode.getExpiration()));
            
            // Check if the redirect URI matches
            // "Ensure that the redirect_uri parameter is present if the
            // redirect_uri parameter was included in the initial authorization
            // request as described in Section 4.1.1, and if included ensure
            // their values are identical
            if(!authorizationCode.getRedirectURI().equals(oauth.getRedirectURI())) {
                oauth.setErrorMessage("### HandleGrantAuthorizationCode.generateToken() Parameter <redirect_uri> doesn't match with the first one");
                LOG.log(Level.SEVERE, oauth.getErrorMessage());
                throw new AccessDeniedException(oauth);
            }
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() <redirect_uri> match");
            
            // Check if the Client ID matches
//            if(!authorizationCode.getClientID().equals(oauth.getClientID())) {
            if(!authorizationCode.getClient().getID().equals(oauth.getClientID())) {
                oauth.setErrorMessage("### HandleGrantAuthorizationCode.getToken() Parameter <client_id> doesn't match with the first one");
                throw new AccessDeniedException(oauth);
            }
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() <client_id> match");
            
            // Issue an Access Token, based on information gathered
            // from the Authorization Code
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() Requesting new Access Token");
//            accessToken = newAccessCode(authorizationCode);
            AccessToken accessToken = new AccessToken(config.getContext(), 
                    authorizationCode, config.getPrincipal());
            
            // Save the Access Code for a Period of Time (Expiration)
            LOG.log(Level.INFO, ">>> [SERVER] generateToken() Storing Access Token and Deleting the AuthorizationCode");
//            messaging.storeAccessToken(accessToken);
            config.getTokenManagement().storeAccessToken(oauth, authorizationCode, accessToken);
            
            return accessToken;
    }

}
