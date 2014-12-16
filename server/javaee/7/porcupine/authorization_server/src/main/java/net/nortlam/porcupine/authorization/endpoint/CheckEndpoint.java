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
package net.nortlam.porcupine.authorization.endpoint;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.authorization.token.TokenManagement;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 * Endpoint used to check if some Access Token is still alive and valid
 *
 * @author Mauricio "Maltron" Leal */
@Path("/check")
public class CheckEndpoint implements Serializable {
    
    @Context
    private ServletContext context;
    
    @Inject
    private TokenManagement tokenManagement;

    private static final Logger LOG = Logger.getLogger(CheckEndpoint.class.getName());

    public CheckEndpoint() {
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAccessToken(@BeanParam OAuth2 oauth) 
                            throws ServerErrorException, 
                                InvalidRequestException, InvalidScopeException {
        LOG.log(Level.INFO, "checkAccessToken() ");
        // Parameters necessary for this particular endpoint
        // access_token
        // scope
        // grant
        LOG.log(Level.INFO, "checkAccessToken() Checking Parameters: "+
                " Access Token:{0} Scope:{1} Grant:{2}", new Object[]{
                    oauth.getAccessToken(), oauth.getScope(),
                oauth.getGrant()});
        oauth.validateParameters(OAuth2.PARAMETER_ACCESS_TOKEN, oauth.getAccessToken(),
                OAuth2.PARAMETER_SCOPE, oauth.getScope(),
                OAuth2.PORCUPINE_PARAMETER_GRANT, oauth.getGrant() != null ? 
                                        oauth.getGrant().toString() : null);
        
        // Check if still exists
//        AccessToken accessToken = messaging.retrieveAccessToken(oauth, 
//                        OAuth2.PARAMETER_ACCESS_TOKEN, oauth.getAccessToken());
        LOG.log(Level.INFO, "checkAccessToken() Retrieving Access Token from Database");
        AccessToken accessToken = tokenManagement.retrieveAccessToken(oauth, oauth.getAccessToken());
        if(accessToken == null) { // NOT FOUND or it has being expired
            oauth.setErrorMessage("### CheckEndpoint.checkAccessToken() Access Token doesn't exist or it has being expired");
            throw new InvalidRequestException(oauth);
        }
        
        // Check if the token is expired
        LOG.log(Level.INFO, "checkAccessToken() Access Token is Expired ?");
        if(accessToken.isExpired(context)) {
            tokenManagement.deleteAccessToken(oauth, accessToken);
            oauth.setErrorMessage("### CheckEndpoint.checkAcessToken() Access Token is EXPIRED");
            throw new InvalidRequestException(oauth);
        }
        
        // Check of this Access Token match the Scope
        LOG.log(Level.INFO, "checkAccessToken() Does the Scope({0}) match "+
                "the one on the Access Token:{1}", new Object[] {oauth.getScope(),
                    accessToken.getScope().getName()});
        if(!accessToken.getScope().getName().equals(oauth.getScope())) {
            oauth.setErrorMessage("### CheckEndpoint.checkAccessToken() Scope doesn't match"+
                    " for this Access Token");
            throw new InvalidScopeException(oauth);
        }
        
        // Check if this Scope has the grant enabled
        LOG.log(Level.INFO, "checkAccessToken() Check if this grant is allowed");
        if(!oauth.isGrantAllowed(accessToken.getScope(), oauth.getGrant())) {
            oauth.setErrorMessage("### CheckEndpoint.checkAccessToken() "+
                    " The Scope is not allowing this Grant");
            throw new InvalidScopeException(oauth);
        }
        
        // Check if this Client is Enabled
        LOG.log(Level.INFO, "checkAccessToken() Is this Client enabled ?");
        if(!accessToken.getScope().getClient().isEnabled()) {
            oauth.setErrorMessage("### CheckEndpoint.checkAccessToken() Client is not"+
                    " enabled.");
            throw new ServerErrorException(oauth);
        }
        
        // If it has returned, means it does exist and it's still valid
        LOG.log(Level.INFO, "checkAccessToken() Everything checks out.");
        return Response.ok(accessToken, MediaType.APPLICATION_JSON).build();
    }
}
