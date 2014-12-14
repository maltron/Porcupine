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

import java.net.URI;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import net.nortlam.porcupine.common.entity.Client;
import net.nortlam.porcupine.common.entity.Scope;
import net.nortlam.porcupine.authorization.service.ClientService;
import net.nortlam.porcupine.authorization.service.UserService;
import net.nortlam.porcupine.authorization.token.TokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.entity.ProtectedResource;
import net.nortlam.porcupine.common.entity.User;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.exception.UnauthorizedClientException;
import net.nortlam.porcupine.common.exception.UnsupportedResponseTypeException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.token.AuthorizationCode;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Path("/allow/{client_id}/{scope}/{grant}")
@Produces(MediaType.APPLICATION_FORM_URLENCODED)
public class AllowEndpoint {

    private static final Logger LOG = Logger.getLogger(AllowEndpoint.class.getName());
    
    @Context
    private ServletContext context;
    
    @Context
    private SecurityContext security;
    
    @Inject
    private TokenManagement tokenManagement;
    
    @EJB
    private UserService userService;
    
    @EJB
    private ClientService clientService;
    
//    @EJB
//    private ScopeService scopeService;
    
    @GET
    public Response validate(@BeanParam OAuth2 oauth, 
            @PathParam(OAuth2.PARAMETER_CLIENT_ID) String clientID,
            @PathParam(OAuth2.PARAMETER_SCOPE) String scopeName,
            @PathParam("grant") Grant grant) 
            throws InvalidRequestException, InvalidScopeException, 
                            UnauthorizedClientException, ServerErrorException,
                                            UnsupportedResponseTypeException {
        LOG.log(Level.INFO, ">>> [SERVER] AllowEndpoint.validate() client_id:{0}"+
                " scope:{1} Grant:{2}", new Object[] {clientID, scopeName, grant});
        
        // First, validate all the necessary parameters
        oauth.validateParameters("client_id", clientID, "scope", scopeName, 
                                                    "grant", grant.toString());

        // Look for information about the Client 
        // PENDING: A beter must be found to use this without querying
        Client client = clientService.findByID(clientID);
        if(client == null) {
            oauth.setErrorMessage("### AllowEndpoint.validate() "+
                    "Unable to find ClientID:");
            throw new UnauthorizedClientException(oauth);
        }
        
        // Is this client enabled ?
        LOG.log(Level.INFO, ">>> [SERVER] Client is enabled ?");
        if(!client.isEnabled()) {
            oauth.setErrorMessage("### AllowEndpoint.validate() "+
                    " ClientID is not enabled");
            throw new UnauthorizedClientException(oauth);
        }

        // Seek for the existence of this Scope for this client
        Scope scope = clientService.doesScopeExistInClient(client, scopeName);
        if(scope == null) {
            oauth.setErrorMessage("### AllowEndpoint.validate() "+
                    " Unable to find this Scope for this Client");
            throw new InvalidScopeException(oauth);
        }
        
        // Is this scope enable for this particular grant ?
        if(!oauth.isGrantAllowed(scope, grant)) {
            oauth.setErrorMessage("### AuthorizeEndpoint.validate() "+
                    "Grant is not allowed for this particular Scope");
            throw new UnsupportedResponseTypeException(oauth);
        }

        URI redirectURI = null;
        // AUTHORIZATION CODE AUTHORIZATION CODE AUTHORIZATION CODE AUTHORIZATION CODE 
        if(grant == Grant.AUTHORIZATION_CODE) { // AUTHORIZATION CODE AUTHORIZATION CODE 

            // Generate a new Authorization Code 
            AuthorizationCode authorizationCode = newAuthorizationCode(context, 
                                            client, scope, oauth.getRedirectURI());

            // Second, store the generated code and the information about the Client
    //        messaging.storeCode(oauth, authorizationCode);
            tokenManagement.storeCode(oauth, authorizationCode);

            UriBuilder builder = UriBuilder.fromUri(oauth.getRedirectURI())
                    .queryParam(OAuth2.PARAMETER_CODE, authorizationCode.getCode());
            if(oauth.hasState())
                    builder.queryParam(OAuth2.PARAMETER_STATE, oauth.getState());
            
            redirectURI = builder.build();
//            redirectURI = URIBuilder.buildURI(oauth.getRedirectURI(), 
//                    OAuth2.PARAMETER_CODE, authorizationCode.getCode(),
//                                    OAuth2.PARAMETER_STATE, oauth.getState());
            LOG.log(Level.INFO, ">>> [SERVER] AllowEndpoint.validate() REDIRECT:{0}", 
                    redirectURI.toString());
            
        // IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT 
        } else if(grant == Grant.IMPLICIT) { // IMPLICIT IMPLICIT IMPLICIT IMPLICIT 
            LOG.log(Level.INFO, ">>> [SERVER] validate() Everything checks out. "+
                    "Generating a new Access Token");
            
            User principal = userService.findByPrincipal(security.getUserPrincipal());
            if(principal == null) {
                oauth.setErrorMessage("### AllowEndpoint.validate() Unable to find"+
                        " Principal on Database");
                throw new ServerErrorException(oauth);
            }
            
            AccessToken accessToken = new AccessToken(context, scope, principal);
            
            // Construct the URI for redirect
            UriBuilder builder = UriBuilder.fromUri(oauth.getRedirectURI());
            builder.queryParam(OAuth2.PARAMETER_ACCESS_TOKEN, accessToken.getToken());
            builder.queryParam(OAuth2.PARAMETER_TOKEN_TYPE, accessToken.getTokenType());
            builder.queryParam(OAuth2.PARAMETER_EXPIRES_IN, accessToken.getExpirationInSeconds());
            if(oauth.hasState())
                builder.queryParam(OAuth2.PARAMETER_STATE, oauth.getState());
            
            // for implicit, the whole query parameter must be in the fragment side
            // So, we're just going to replace ? -> #
            redirectURI = URI.create(builder.build().toString().replace('?', '#'));
            
//        String parameters = URIBuilder.buildParameters(
//                OAuth2.PARAMETER_ACCESS_TOKEN, getToken(),
//                OAuth2.PARAMETER_REFRESH_TOKEN, getRefreshToken(),
//                OAuth2.PARAMETER_TOKEN_TYPE, getTokenType(),
//                // Section 4.2.2 expires_in: The value in seconds 
//                OAuth2.PARAMETER_EXPIRES_IN, toSeconds(getExpiration()),
//                OAuth2.PARAMETER_STATE, state);
            
//            redirectURI = accessToken.toImplicitURI(oauth.getRedirectURI(), oauth.getState());
            LOG.log(Level.INFO, ">>> [SERVER] validate() Redirect URI:{0}", redirectURI);
        }

        return Response.seeOther(redirectURI).build();
    }
    
//    public void validatePathParameters(OAuth2 oauth, String clientID, 
//            String scopeName, Grant grant) throws InvalidRequestException {
////        // code : REQUIRED (Porcupine2 specific as PathParam)
////        if(code == null) {
////            oauth.setErrorMessage("### AllowEndpoint.validatePathParametersForAllowEndpoint() PathParameter({code}) is missing [PORCUPINE 2 SPECIFIC]");
////            LOG.log(Level.SEVERE, oauth.getErrorMessage());
////            throw new InvalidRequestException(oauth);
////        }
//        
//        if(clientID == null) {
//            oauth.setErrorMessage("### AllowEndpoint.validatePathParametersForAllowEndpoint() PathParameter({client_id}) is missing [PORCUPINE 2 SPECIFIC]");
//            throw new InvalidRequestException(oauth);
//        }
//        
//        // redirect_uri: REQUIRED
//        if(oauth.getRedirectURI() == null) {
//            oauth.setErrorMessage("### AllowEndpoint.validatePathParametersForAllowEndpoint() Parameter <redirect_uri> is missing [PORCUPINE 2 SPECIFIC]");
//            throw new InvalidRequestException(oauth);
//        }
//        
//        // scopeID: REQURIED
//        if(scopeName == null) {
//            oauth.setErrorMessage("### AllowEndpoint.validatePathParameters() Parameter <scope> can not be less then zero [PORCUPINE 2 SPECIFIC]");
//            throw new InvalidRequestException(oauth);
//        }
//        
//        // grant: REQUIRED
//        if(grant == null) {
//            oauth.setErrorMessage("### AllowEndpoint.validatePathParameters() Parameter<grant> is missing  [PORCUPINE 2 SPECIFIC]");
//            throw new InvalidRequestException(oauth);
//        }
//        
//        // state: OPTIONAL
//        
//    }
    
    private AuthorizationCode newAuthorizationCode(ServletContext context, Client client, 
                                             Scope scope, String redirectURI) {
        String[] resources = turnResourcesIntoArray(scope);
        
        // Get the Expiration timming
        TimeZone zone = InitParameter.parameterTimeZone(context);
        Locale locale = InitParameter.parameterLocale(context);
        int expiration = InitParameter.parameterAuthorizationCodeExpiration(context);
        
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.add(Calendar.MILLISECOND, expiration);
        
        return new AuthorizationCode(client, scope, redirectURI, calendar.getTime());
//        return new AuthorizationCode(clientID, scope.getName(), 
//                                    scope.getExpiration(), resources, redirectURI);
    }

    public String[] turnResourcesIntoArray(Scope scope) {
        if(!scope.isProtectedResources()) return null;
        
        Set<ProtectedResource> protectedResources = scope.getProtectedResources();
        String[] result = new String[protectedResources.size()]; int i = 0;
        for(ProtectedResource protectedResource: protectedResources)
            result[i++] = protectedResource.getResource();
        
        return result;
    }


}
