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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.common.entity.Client;
import net.nortlam.porcupine.common.entity.Scope;

import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.authorization.service.ClientService;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.exception.InvalidRequestException;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.UnauthorizedClientException;
import net.nortlam.porcupine.common.exception.UnsupportedResponseTypeException;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Path("/authorize")
public class AuthorizeEndpoint {
    
    private static final Logger LOG = Logger.getLogger(AuthorizeEndpoint.class.getName());

    @EJB
    private ClientService clientService;
    
    @Context
    private ServletContext context;
    
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response validate(@BeanParam OAuth2 oauth) 
            throws InvalidRequestException, UnauthorizedClientException, 
            AccessDeniedException, InvalidScopeException, UnsupportedResponseTypeException {
//        LOG.log(Level.INFO,">>> [SERVER] getAuthenticationScheme():{0}", context.getAuthenticationScheme());
//        LOG.log(Level.INFO,">>> [SERVER] getUserPrincipal():{0}", context.getUserPrincipal());
//        LOG.log(Level.INFO,">>> [SERVER] isSecure():{0}",context.isSecure());
//        LOG.log(Level.INFO,">>> [SERVER] redirect_uri:{0}",oauth.getRedirectURI());
        
        // First try to identify which grant is
        Grant grant = oauth.getResponseType(); String approvalMessage = null;
        
        // Check if all the necessary parameters are in place
        // (In case of error, a InvalidRequestException will be throw)
        LOG.log(Level.INFO, ">>> [SERVER] Check if all the parameters are in place");
        oauth.validateAuthorizeParametersFor(grant); // AUTHORIZE_CODE or IMPLICIT
        
        // Check for the existence of this client
        LOG.log(Level.INFO, ">>> [SERVER] Check for the existence of this client");
        Client client = clientService.findByID(oauth.getClientID());
        if(client == null) {
            oauth.setErrorMessage("### AuthorizeEndpoint.validate() "+
                    "Unable to find ClientID:");
            throw new UnauthorizedClientException(oauth);
        }
        
        // Is this client enabled ?
        LOG.log(Level.INFO, ">>> [SERVER] Client is enabled ?");
        if(!client.isEnabled()) {
            oauth.setErrorMessage("### AuthorizeEndpoint.validate() "+
                    " ClientID is not enabled");
            throw new UnauthorizedClientException(oauth);
        }
        
        // Seek for the existence of this Scope for this client
        LOG.log(Level.INFO, ">>> [SERVER] Does Scope({0}) exist for this Client ?",oauth.getScope());
        Scope scope = clientService.doesScopeExistInClient(client, oauth.getScope());
        if(scope == null) {
            oauth.setErrorMessage("### AuthorizeEndpoint.validate() "+
                    " Unable to find this Scope for this Client");
            throw new InvalidScopeException(oauth);
        }
        
        // Is this scope enable for this particular grant ?
        LOG.log(Level.INFO, ">>> [SERVER] This Grant {0} is allowed ?", grant);
        if(!oauth.isGrantAllowed(scope, grant)) {
            oauth.setErrorMessage("### AuthorizeEndpoint.validate() "+
                    "Grant is not allowed for this particular Scope");
            throw new UnsupportedResponseTypeException(oauth);
        }
        
//        // Redirect URI's Path
//        String path = oauth.path(oauth.getRedirectURI());
//        
//        LOG.log(Level.INFO, ">>> [SERVER] Path({0}) exist in Scope({1}) ?",
//                new Object[] {path, oauth.getScope()});
//        if(!clientService.existProtectedResourceInScope(scope, path)) {
//                oauth.setErrorMessage("### AuthorizeEndpoint.validate() Redirect URI "+
//                        " doesn't belong to that particular Scope");
//                throw new InvalidScopeException(oauth);
//        }
        
        // Everything checks out. Generating the Message for approval
        LOG.log(Level.INFO, ">>> [SERVER] AuthorizeEndpoint.validate() "+
                                        " Generating final message for approval");
        approvalMessage = approvalMessageForAuthorizationCode(oauth,client, 
                                               scope, grant);
        
//        // Authorization Code 
//        if(grant == Grant.AUTHORIZATION_CODE) { 
//            LOG.info(">>> [SERVER] AuthorizeEndpoint.validate() Authorization Code Selected");
//            
//            // Check if all the necessary parameters are in place
//            oauth.validateAuthorizeParametersFor(grant); // AUTHORIZATION CODE or IMPLICIT
//            LOG.info(">>> [SERVER] AuthorizeEndpoint.validate() All parameters are in place");
//            
//            // First, check the existence of client_id
//            Client client = clientService.findByID(oauth.getClientID());
//            if(client == null) {
//                oauth.setErrorMessage("### AuthorizeEndpoint.validate() "+
//                        "Unable to find ClientID");
//                throw new UnauthorizedClientException(oauth);
//            }
//            
//            // is Client disabled ? (due lack of payment or whatever)
//            if(!client.isEnabled()) {
//                oauth.setErrorMessage("### [SERVER] AuthorizeEndpoint.validate() Client <"+client.getName()+"> is disabled");
//                throw new UnauthorizedClientException(oauth);
//            }
//            
//            // Check if the Scope exists
//            Scope scope = clientService.doesScopeExistInClient(client, oauth.getScope());
//            if(scope == null) { // Didn't find the scope 
//                oauth.setErrorMessage("### [SERVER] AuthorizeEndpoint.validate() Unable to find Scope:"+oauth.getScope());
//                throw new InvalidScopeException(oauth);
//            }
//            
//            // Is this scope enabled to work with Authorization Code Grant ?
//            if(!scope.isAuthorizationCodeGrant()) {
//                oauth.setErrorMessage("### [SERVER] AuthorizeEndpoint.validate() Scope:<"+oauth.getScope()+"> does not support the Authorization Code Grant");
//                throw new UnsupportedResponseTypeException(oauth);
//            }
//
//            URI uriRedirectTo = URIBuilder.buildURI(oauth.getRedirectURI());
//            
//            // Check if the Redirect URI belongs to that particular Scope
//            if(!clientService.existProtectedResourceInScope(scope, uriRedirectTo.getPath())) {
//                oauth.setErrorMessage("### AuthorizeEndpoint.validate() Redirect URI "+
//                        " doesn't belong to that particular Scope");
//                throw new InvalidScopeException(oauth);
//            }
//            
//            // Builds the message
//            approvalMessage = approvalMessageForAuthorizationCode(oauth,client, 
//                                                        scope, Grant.AUTHORIZATION_CODE);
//            
//        // IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT 
//        //  IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT IMPLICIT 
//        } else if(grant == Grant.IMPLICIT) {
//            LOG.log(Level.INFO, ">>> [SERVER] validate() IMPLICIT CODE SELECTED");
//            LOG.log(Level.INFO, ">>> [SERVER] validate() Client ID:{0}", oauth.getClientID());
//            LOG.log(Level.INFO, ">>> [SERVER] validate() Redirect URI:{0}", oauth.getRedirectURI());
//            LOG.log(Level.INFO, ">>> [SERVER] validate() Scope:{0}", oauth.getScope());
//            LOG.log(Level.INFO, ">>> [SERVER] validate() State:{0}", oauth.getState());
//            
////            GET /authorize?response_type=token&
////            client_id=s6BhdRkqt3&
////            state=xyz&
////            redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
////            Host: server.example.com            
//            
//            // Implicit
//            // Check if all the necessary parameters are there
//            LOG.log(Level.INFO, ">>> [SERVER validate() Checking Parameters");
//            oauth.validateAuthorizeParametersFor(grant);
//            
//            // First, check if the Client does exist and it's enabled
//            Client client = clientService.findByID(oauth.getClientID());
//            if(client == null) {
//                oauth.setErrorMessage("### AuthorizeEndpoint.validate() "+
//                        "Unable to find ClientID");
//                throw new UnauthorizedClientException(oauth);
//            }
//            
//            
//            if(!client.isEnabled()) {
//                LOG.log(Level.SEVERE, "### validate() Client doesn't exist but it's not enabled");
//                throw new AccessDeniedException(oauth);
//            }
//            
//            // Check if the Scope does exist
//            Scope scope = clientService.doesScopeExistInClient(client, oauth.getScope());
//            if(scope == null) {
//                oauth.setErrorMessage("### AuthorizeEndpoint.validate() Scope doesn't exist");
//                throw new InvalidScopeException(oauth);
//            }
//            
//            // Is this Scope valid for this Grant ?
//            if(!scope.isImplicitGrant()) {
//                oauth.setErrorMessage("### AuthorizeEndpoint.validate() Client "+
//                        " not allowed to work with Implicit Grant");
//                throw new UnsupportedResponseTypeException(oauth);
//            }
//            
//            URI uriRedirectTo = URIBuilder.buildURI(oauth.getRedirectURI());
//            
//            // Check if the Redirect URI belongs to that particular Scope
//            if(!clientService.existProtectedResourceInScope(scope, uriRedirectTo.getPath())) {
//                oauth.setErrorMessage("### AuthorizeEndpoint.validate() Redirect URI "+
//                        " doesn't belong to that particular Scope");
//                throw new InvalidScopeException(oauth);
//            }
//            
//            // Builds the message
//            approvalMessage = approvalMessageForAuthorizationCode(oauth,client, 
//                                                        scope, Grant.IMPLICIT);
//        }
        
        return Response.ok(approvalMessage, MediaType.TEXT_HTML).build();
    }
    
    private String approvalMessageForAuthorizationCode(OAuth2 oauth, 
            Client client, Scope scope, Grant grant) {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");
        builder.append("<head>");
        builder.append("<title>Porcupine: Authorization Server OAuth 2.0</title>");
        builder.append("<meta charset=\"UTF-8\">");
        builder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("<h1>");
            builder.append("<div>An application ");
            builder.append(client.getName());
            builder.append(" would like to connect to your account<br/><br/></div>");
            builder.append("<div>");
            builder.append(scope.getMessage());
            builder.append("<br/><br/></div>");
            builder.append("<div>Allow ");
            builder.append(client.getName());
            builder.append(" access ?<br/><br/></div>");
            builder.append("<div><a href=\"DENY\">DENY</a><br/>");
            
            // ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW 
            //  ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW ALLOW 
            // Allow Endpoint
            builder.append("<a href=allow\\");
            // Client ID
            builder.append(client.getID());
            builder.append("\\");
            // Scope Selected
            builder.append(scope.getName());
            builder.append("\\");
            // Grant Selected
            builder.append(grant.toString());
            builder.append("?");
            // Redirect URI
            builder.append(OAuth2.PARAMETER_REDIRECT_URI);
            builder.append("=");
            builder.append(oauth.getRedirectURI());
            if(oauth.hasState()) {
                builder.append("&");
                builder.append(OAuth2.PARAMETER_STATE);
                builder.append("=");
                builder.append(oauth.getState());
            }
            
            builder.append(">ALLOW</a></div>");
        builder.append("</h1>");
        builder.append("</body>");
        builder.append("</html>");
        
        return builder.toString();
    }
}
