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
import net.nortlam.porcupine.authorization.token.Database;
import net.nortlam.porcupine.authorization.token.TokenManagement;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 * Endpoint used to check if some Access Token is still alive
 *
 * @author Mauricio "Maltron" Leal */
@Path("/check")
public class CheckEndpoint implements Serializable {
    
//    @EJB
//    private MessagingService messaging;
    
    @Context
    private ServletContext context;
    
    @Inject @Database
    private TokenManagement tokenManagement;

    private static final Logger LOG = Logger.getLogger(CheckEndpoint.class.getName());

    public CheckEndpoint() {
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAccessToken(@BeanParam OAuth2 oauth) 
                            throws ServerErrorException, InvalidRequestException {
        // Check if the parameter is available
        if(oauth.getAccessToken() == null) {
            oauth.setErrorMessage("### CheckEndpoint.checkAccessToken() Parameter <access_token> is missing");
            throw new InvalidRequestException(oauth);
        }
        
        // Check if still exists
//        AccessToken accessToken = messaging.retrieveAccessToken(oauth, 
//                        OAuth2.PARAMETER_ACCESS_TOKEN, oauth.getAccessToken());
        AccessToken accessToken = tokenManagement.retrieveAccessToken(oauth, oauth.getAccessToken());
        if(accessToken == null) { // NOT FOUND or it has being expired
            oauth.setErrorMessage("### CheckEndpoint.checkAccessToken() Access Token doesn't exist or it has being expired");
            throw new InvalidRequestException(oauth);
        }
        
        // Check if the token is expired
        if(accessToken.isExpired(context)) {
            tokenManagement.deleteAccessToken(oauth, accessToken);
            oauth.setErrorMessage("### CheckEndpoint.checkAcessToken() Access Token is EXPIRED");
            throw new InvalidRequestException(oauth);
        }
        
        // If it has returned, means it does exist and it's still valid
        return Response.ok(accessToken, MediaType.APPLICATION_JSON).build();
    }
}
