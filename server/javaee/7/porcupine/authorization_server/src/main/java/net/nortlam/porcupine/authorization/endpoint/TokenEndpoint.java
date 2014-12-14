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
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import net.nortlam.porcupine.authorization.service.ClientService;
import net.nortlam.porcupine.authorization.service.UserService;
import net.nortlam.porcupine.authorization.token.TokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.entity.User;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.exception.UnauthorizedClientException;
import net.nortlam.porcupine.common.exception.UnsupportedResponseTypeException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.server.handlegrant.HandleGrant;
import net.nortlam.porcupine.server.handlegrant.HandleGrantConfiguration;
import net.nortlam.porcupine.server.handlegrant.HandleGrantFactory;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Path("/token")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TokenEndpoint implements Serializable, HandleGrantConfiguration {

    private static final Logger LOG = Logger.getLogger(TokenEndpoint.class.getName());
    private OAuth2 oauth;
    private User principal;

    @Context
    private ServletContext context;
    
    @Context
    private SecurityContext security;
    
    @EJB
    private UserService userService;
    
    @EJB 
    private ClientService clientService;
    
    @Inject
    private TokenManagement tokenManagement;
    
    @POST
    public Response getToken(@BeanParam OAuth2 oauth) 
            throws InvalidRequestException, InvalidScopeException, 
                ServerErrorException, UnauthorizedClientException,
                        AccessDeniedException, UnsupportedResponseTypeException {
        this.oauth = oauth;
        LOG.log(Level.INFO, ">>> [SERVER] TokenEndpoint.getToken() grant_type:{0}"+
                " code:{1} redirect_uri:{2} client_id:{3}",
                new Object[] {oauth.getGrantType(), oauth.getCode(), 
                                    oauth.getRedirectURI(), oauth.getClientID()});
        // First, check which grant is referring to
        Grant grant = oauth.getGrantType();
        
        // Check if all the paramaters are in place
        LOG.log(Level.INFO, ">>> [SERVER] getToken() Checking if all"+
                " parameters are in place Grant:{0}", grant);
        oauth.validateTokenParametersFor(grant);
        
        LOG.log(Level.INFO, ">>> [SERVER] Looking for Principal:{0}", security.getUserPrincipal());
        principal = userService.findByPrincipal(security.getUserPrincipal());
        if(principal == null) {
            oauth.setErrorMessage("### TokenEndpoint.getToken() Unable to "+
                    " find Principal:"+security.getUserPrincipal());
            throw new ServerErrorException(oauth);
        }

        HandleGrantFactory factory = new HandleGrantFactory(grant, this);
        HandleGrant handleGrant = factory.chooseGrant();
        AccessToken accessToken = handleGrant.generateToken();
        
        LOG.log(Level.INFO, ">>> [SERVER] getToken() Returning Access Token:{0}", 
                            accessToken != null ? accessToken.getToken() : "NULL");
       return Response.ok(accessToken).build();
    }
    
    // CONFIGURATION CONFIGURATION CONFIGURATION CONFIGURATION CONFIGURATION CONFIGURATION 
    //   CONFIGURATION CONFIGURATION CONFIGURATION CONFIGURATION CONFIGURATION CONFIGURATION 
    @Override
    public OAuth2 getOAuth() {
        return oauth;
    }

    @Override
    public ServletContext getContext() {
        return context;
    }

    @Override
    public ClientService getClientService() {
        return clientService;
    }

    @Override
    public TokenManagement getTokenManagement() {
        return tokenManagement;
    }

    @Override
    public User getPrincipal() {
        return principal;
    }
}
