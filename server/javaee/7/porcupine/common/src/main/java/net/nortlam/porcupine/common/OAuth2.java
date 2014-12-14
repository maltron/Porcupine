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
package net.nortlam.porcupine.common;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;
import net.nortlam.porcupine.common.entity.Scope;

import net.nortlam.porcupine.common.exception.InvalidRequestException;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class OAuth2 implements Serializable {
    
    private static final Logger LOG = Logger.getLogger(OAuth2.class.getName());

    public static final String PARAMETER_RESPONSE_TYPE = "response_type";
    public static final String PARAMETER_RESPONSE_TYPE_CODE = "code";
    public static final String PARAMETER_RESPONSE_TYPE_TOKEN = "token";
    public static final String PARAMETER_CLIENT_ID = "client_id";
    public static final String PARAMETER_REDIRECT_URI = "redirect_uri";
    public static final String PARAMETER_SCOPE = "scope";
    public static final String PARAMETER_STATE = "state";
    public static final String PARAMETER_REFRESH_TOKEN = "refresh_token";
    
    public static final String PARAMETER_CODE = "code";
    
    public static final String PARAMETER_GRANT_TYPE = "grant_type";
    public static final String PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    public static final String PARAMETER_GRANT_TYPE_PASSWORD = "password";
    public static final String PARAMETER_GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String PARAMETER_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    
    public static final String PARAMETER_USERNAME = "username";
    public static final String PARAMETER_PASSWORD = "password";

    public static final String PARAMETER_ACCESS_TOKEN = "access_token";
    public static final String PARAMETER_TOKEN_TYPE = "token_type";
    public static final String PARAMETER_EXPIRES_IN = "expires_in";
    
    public static final String HEADER_AUTHORIZATION = "Authorization";
    
    public static final String PARAMETER_ERROR = "error";
    public static final String PARAMETER_ERROR_INVALID_REQUEST = "invalid_request";
    public static final String PARAMETER_DESCRIPTION_INVALID_REQUEST = 
            "The request is missing a required parameter, includes an"+
            " invalid parameter value, includes a parameter more than "+
            "once, or is otherwise malformed";
    public static final String PARAMETER_ERROR_UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static final String PARAMETER_DESCRIPTION_UNAUTHORIZED_CLIENT =
            "The client is not authorized to request an authorization code"+
            " using this method";
    public static final String PARAMETER_ERROR_ACCESS_DENIED = "access_denied";
    public static final String PARAMETER_DESCRIPTION_ACCESS_DENIED = 
            "The resource owner or authorization server denied the request";
    public static final String PARAMETER_ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    public static final String PARAMETER_DESCRIPTION_UNSUPPORTED_RESPONSE_TYPE =
            "The authorization server does not support obtaining an"+
            " authorization code using this method";
    public static final String PARAMETER_ERROR_INVALID_SCOPE = "invalid_scope";
    public static final String PARAMETER_DESCRIPTION_INVALID_SCOPE = 
            "The requested scope is invalid, unknown, or malformed";
    public static final String PARAMETER_ERROR_SERVER_ERROR = "server_error";
    public static final String PARAMETER_DESCRIPTION_SERVER_ERROR = 
            "The authorization server encountered an unexpected"+
            " condition that prevented it from fulfilling the "+
            "request. (This error code is needed because a 500"+
            " Internal Server Error HTTP status code cannot be"+
            " returned to the client via an HTTP redirect.)";
    public static final String PARAMETER_ERROR_TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    public static final String PARAMETER_ERROR_DESCRIPTION_TEMPORARILY_UNAVAILABLE =
            "The authorization server is currently unable to handle "+
            "the request due to an temporary overloading or maintenance"+
            " of the server. (This error code is needed because a 503"+
            " Service Unavailable HTTP status code cannot be returned"+
            " to the client via an HTTP redirect.)";
    
    public static final String PARAMETER_ERROR_DESCRIPTION = "error_description";
    public static final String PARAMETER_ERROR_URI = "error_uri";
    
    @QueryParam(PARAMETER_RESPONSE_TYPE)
    private String query_response_type; 
    
    @FormParam(PARAMETER_RESPONSE_TYPE)
    private String form_response_type;
    
    @QueryParam(PARAMETER_CLIENT_ID)
    private String query_client_id;
    
    @FormParam(PARAMETER_CLIENT_ID)
    private String form_client_id;
    
    @QueryParam(PARAMETER_REDIRECT_URI)
    private String query_redirect_uri;
    
    @FormParam(PARAMETER_REDIRECT_URI)
    private String form_redirect_uri;
    
    @QueryParam(PARAMETER_SCOPE)
    private String query_scope;
    
    @FormParam(PARAMETER_SCOPE)
    private String form_scope;
    
    @QueryParam(PARAMETER_STATE)
    private String query_state;
    
    @FormParam(PARAMETER_STATE)
    private String form_state;
    
    @QueryParam(PARAMETER_CODE)
    private String query_code;
    
    @FormParam(PARAMETER_CODE)
    private String form_code;
    
    @QueryParam(PARAMETER_GRANT_TYPE)
    private String query_grant_type;
    
    @FormParam(PARAMETER_GRANT_TYPE)
    private String form_grant_type;
    
    @QueryParam(PARAMETER_REFRESH_TOKEN)
    private String query_refresh_token;
    
    @FormParam(PARAMETER_REFRESH_TOKEN)
    private String form_refresh_token;
    
    @QueryParam(PARAMETER_USERNAME)
    private String query_username;
    
    @FormParam(PARAMETER_USERNAME)
    private String form_username;
    
    @QueryParam(PARAMETER_PASSWORD)
    private String query_password;
    
    @FormParam(PARAMETER_PASSWORD)
    private String form_password;
    
    @QueryParam(PARAMETER_ACCESS_TOKEN)
    private String query_access_token;
    
    @FormParam(PARAMETER_ACCESS_TOKEN)
    private String form_access_token;
    
    private String errorMessage;
    
    public OAuth2() {
    }
    
    /**
     * Identify which Grant based on response_type
     * Possible answers: code and token
     * Used only on Authorization Endpoint */
    public Grant getResponseType() throws InvalidRequestException {
        String responseType = notNull(query_response_type, form_response_type);
        LOG.log(Level.INFO, ">>> OAuth2.getResponseType() response_type:{0}", responseType);
        
        if(responseType == null) {
            setErrorMessage("### OAuth2.identifyBasedResponseType() Missing parameter <response_type>");
            throw new InvalidRequestException(this);
            
        } else if(responseType.equals(PARAMETER_RESPONSE_TYPE_CODE))
            return Grant.AUTHORIZATION_CODE;
        else if(responseType.equals(PARAMETER_RESPONSE_TYPE_TOKEN))
            return Grant.IMPLICIT;

        setErrorMessage("### OAuth2.identifyBasedResponseType() Unable to identify a suitable value for <response_type>. Possible values are: response_type=code or response_type=token");
        throw new InvalidRequestException(this);
    }
    
    /**
     * Identify which Grant based on grant_type
     * Possible answers: authorization_code, password or client_credentials
     * Used only on Token Endpoint */
    public Grant getGrantType() throws InvalidRequestException {
        String grantType = notNull(query_grant_type, form_grant_type);
        LOG.log(Level.INFO, ">>> OAuth2.getGrantType() grant_type:{0}", grantType);
        
        if(grantType == null) {
            setErrorMessage("### OAuth2.identifyBasedGrantType() Missing Parameter <grant_type>");
            throw new InvalidRequestException(this);
            
        } else if(grantType.equals(PARAMETER_GRANT_TYPE_AUTHORIZATION_CODE))
            return Grant.AUTHORIZATION_CODE;
        
        else if(grantType.equals(PARAMETER_GRANT_TYPE_PASSWORD))
            return Grant.RESOURCE_OWNER_PASSWORD_CREDENTIALS;
        
        else if(grantType.equals(PARAMETER_GRANT_TYPE_CLIENT_CREDENTIALS))
            return Grant.CLIENT_CREDENTIALS;
        
        else if(grantType.equals(PARAMETER_GRANT_TYPE_REFRESH_TOKEN))
            return Grant.REFRESH_TOKEN; // Not necessary a Grant though
        
        setErrorMessage("### OAuth2.identifyBasedGrantType() Unable to identify a suitable value for <grant_type>. Possible values are: grant_type=authorization_code, grant_type=password or grant_type=client_credentials");
        throw new InvalidRequestException(this);
    }
    
    /**
     * Used in Authorize Endpoint only */
    public void validateAuthorizeParametersFor(Grant grant) throws InvalidRequestException {
        String missing = null;
        switch(grant) {
            case AUTHORIZATION_CODE:
                missing = missingParameter(
                // response_type: REQUIRED
                "response_type", notNull(query_response_type, form_response_type),
                // client_id: REQUIRED
                "client_id", notNull(query_client_id, form_client_id),
                // redirect_uri: OPTIONAL (Porcupine: REQUIRED)
                "redirect_uri", notNull(query_redirect_uri, form_redirect_uri),
                // scope: OPTIONAL (Porcupine: REQUIRED)
                "scope", notNull(query_scope, form_scope));
                // state: RECOMMENDED
                break;
            case IMPLICIT:
                missing = missingParameter(
                // response_type: REQUIRED
                "response_type", notNull(query_response_type, form_response_type),
                // client_id: REQUIRED
                "client_id", notNull(query_client_id, form_client_id),
                // redirect_uri: OPTIONAL (Porcupine: REQUIRED)
                "redirect_uri", notNull(query_redirect_uri, form_redirect_uri),
                // scope: OPTIONAL (Porcupine: REQUIRED)
                "scope", notNull(query_scope, form_scope));
                // state: RECOMMENDED
        }

        // Did it find any error message ?
        if(missing != null && missing.length() > 0) {
            setErrorMessage(String.format(
                    "### OAuth2.validateAuthorizeParametersFor(%s)"+
                    " Missing Parameters:%s", grant, missing));
            throw new InvalidRequestException(this);
        }
    }
    
    
    /**
     * Used in Token Endpoint only */
    public void validateTokenParametersFor(Grant grant) throws InvalidRequestException {
        String missing = null;
        switch(grant) {
            case REFRESH_TOKEN: 
                missing = missingParameter(
                        // grant_type: REQUIRED
                        "grant_type", notNull(query_grant_type, form_grant_type),
                        // refresh_token: REQUIRED
                        "refresh_token", notNull(query_refresh_token, form_refresh_token),
                        // scope: OPTIONAL (Porcupine: REQUIRED)
                        "scope", notNull(query_scope, form_scope));
                break;
            case AUTHORIZATION_CODE: 
                missing = missingParameter(
                        // grant_type: REQUIRED
                        "grant_type", notNull(query_grant_type, form_grant_type),
                        // code: REQUIRED
                        "code", notNull(query_code, form_code),
                        // redirect_uri: REQUIRED
                        "redirect_uri", notNull(query_redirect_uri, form_redirect_uri),
                        // client_id: REQUIRED
                        "client_id", notNull(query_client_id, form_client_id));
                break;
            case RESOURCE_OWNER_PASSWORD_CREDENTIALS:
                missing = missingParameter(
                        // grant_type: REQUIRED
                        "grant_type", notNull(query_grant_type, form_grant_type),
                        // username: REQUIRED
                        "username", notNull(query_username, form_username),
                        // password: REQUIRED
                        "password", notNull(query_password, form_password),
                        // scope: OPTIONAL (Porcupine: REQUIRED)
                        "scope", notNull(query_scope, form_scope));
                break;
            case CLIENT_CREDENTIALS:
                missing = missingParameter(
                        // grant_type: REQUIRED
                        "grant_type", notNull(query_grant_type, form_grant_type),
                        // scope: OPTIONAL (Porcupine: REQUIRED)
                        "scope", notNull(query_scope, form_scope));
                break;
        }
        
        // ERROR: Invalid Request Exception
        if(missing != null && missing.length() > 0) {
            setErrorMessage(String.format("### OAuth2.validateTokenParametersFor(%s)"+
                                            " Missing Parameters:%s", grant, missing));
            throw new InvalidRequestException(this);
        }
    }
    
    public void validateParameters(String ... parameter) throws InvalidRequestException {
        String missing = missingParameter(parameter);
        
        if(missing != null && missing.length() > 0) {
            setErrorMessage(String.format("### OAuth2.validateParameters() "+
                    "Missing Parameters:{0}", missing));
            throw new InvalidRequestException(this);
        }
    }
    
    private String missingParameter(String ... parameter) {
        StringBuilder missing = new StringBuilder();
        for(int i=0; i < parameter.length; i += 2) {
            if(parameter[i+1] == null) 
                missing.append(parameter[i]).append(",");
        }
        // Remove the last comma
        if(missing.length() > 0) missing.deleteCharAt(missing.length()-1);
        
        return missing.length() > 0 ? missing.toString() : null;
    }
    
    public boolean isGrantAllowed(Scope scope, Grant grant) {
        boolean allowed = false;
        switch(grant) {
            case AUTHORIZATION_CODE: 
                allowed = scope.isAuthorizationCodeGrant(); break;
            case IMPLICIT:
                allowed = scope.isImplicitGrant(); break;
            case RESOURCE_OWNER_PASSWORD_CREDENTIALS:
                allowed = scope.isResourceOwnerPasswordCredentials(); break;
            case CLIENT_CREDENTIALS: 
                allowed = scope.isClientCredentials(); break;
        }
        
        return allowed;
    }
    
    public String path(String url) {
        return UriBuilder.fromUri(url).build().getPath();
    }
    
//    /**
//     * Section 6: Refreshing an Access Token
//     * If the authorization server issued a refresh token to the client,
//     * the client makes a refresh request to the token endpoint by adding
//     * the following parameters using the "application/x-www-form-urlencoded"
//     * format per Appendix B with a character encoding of UTF-8 in the HTTP
//     * request entity-body: */
//    public void validateParametersForRefreshToken() throws InvalidRequestException {
//        String grantType = notNull(query_grant_type, form_grant_type);
//        String refreshToken = notNull(query_refresh_token, form_refresh_token);
//        LOG.log(Level.INFO, ">>> OAuth2.validateParametersForRefreshToken() grant_type:{0} refresh_token:{0}",
//                                        new Object[] {grantType, refreshToken});
//        
//        // grant_type: REQUIRED
//        if(grantType == null) {
//            setErrorMessage("### OAuth2.validateParametersForRefreshToken() Parameter <grant_type> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // refresh_token: REQUIRED
//        if(refreshToken == null) {
//            setErrorMessage("### OAuth2.validateParametersForRefreshToken() Parameter <refresh_token> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // scope: OPTIONAL (Porcupine: REQUIRED)
//        if(notNull(query_scope, form_scope) == null) {
//            setErrorMessage("### OAuth2.validateParametersForRefreshToken() Parameter <scope> is missing");
//            throw new InvalidRequestException(this);
//        }
//    }
//    
//    
//    /**
//     * Validates every single parameter request
//     * based on Section 4.1.1 Authorization Request
//     * for this Authorization Code Flow */
//    public void validateParametersForAuthorizationCodeOnAuthorizationEndpoint() 
//                                                    throws InvalidRequestException {
//        // response_type: REQUIRED
//        if(notNull(query_response_type, form_response_type) == null) {
//            setErrorMessage("### OAuth2.validateParametersForAuthorizationCodeOnAuthorizationEndpoint() Parameter <response_type> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // client_id: REQUIRED
//        if(notNull(query_client_id, form_client_id) == null) {
//            setErrorMessage("### OAuth2.validateParametersForAuthorizationCodeOnAuthorizationEndpoint() Parameter <client_id> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // redirect_uri: OPTIONAL (Porcupine : REQUIRED)
//        if(notNull(query_redirect_uri, form_redirect_uri) == null) {
//            setErrorMessage("### OAuth2.validateParametersForAuthorizationCodeOnAuthorizationEndpoint() Parameter <redirect_uri> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // scope: OPTIONAL (Porcupine: REQUIRED)
//        if(notNull(query_scope, form_scope) == null) {
//            setErrorMessage("### OAuth2.validateParametersForAuthorizationCodeOnAuthorizationEndpoint() Parameter <scope> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // state: RECOMMENDED
//    }
// 
//    /**
//     * Section 4.1.3: Access Token Request
//     * Check if all the necessary parameters are present */
//    public void validateParametersForAuthorizationCodeOnTokenEndpoint() 
//                                                    throws InvalidRequestException {
//        // grant_type: REQUIRED
//        if(notNull(query_grant_type, form_grant_type) == null) {
//            setErrorMessage("### OAuth2.validateParametersForAuthorizationCodeOnTokenEndpoint() Parameter <grant_type> is missing");
//            throw new InvalidRequestException(this);
//        }
//        // code: REQUIRED
//        if(notNull(query_code, form_code) == null) {
//            setErrorMessage("### OAuth2.validateParametersForAuthorizationCodeOnTokenEndpoint() Parameter <code> is missing");
//            throw new InvalidRequestException(this);
//        }
//        // redirect_uri: REQUIRED
//        if(notNull(query_redirect_uri, form_redirect_uri) == null) {
//            setErrorMessage("### OAuth2.validateParametersForAuthorizationCodeOnTokenEndpoint() Parameter <redirect_uri> is missing");
//            throw new InvalidRequestException(this);
//        }
//        // client_id: REQUIRED
//        if(notNull(query_client_id, form_client_id) == null) {
//            setErrorMessage("\"### OAuth2.validateParametersForAuthorizationCodeOnTokenEndpoint() Parameter <client_id> is missing");
//            throw new InvalidRequestException(this);
//        }
//    }
//    
//    /**
//     * Section 4.2.1: Authorization Request (for Implicit) 
//     * Check if the all the necessary parameters are present
//     * @throws net.nortlam.porcupine.common.exception.InvalidRequestException */
//    public void validateParametersForImplicit() throws InvalidRequestException {
//        // response_type: REQUIRED
//        if(notNull(query_response_type, form_response_type) == null) {
//            setErrorMessage("### OAuth2.validateParametersForImplicit() Parameter <response_type> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // client_id: REQUIRED
//        if(notNull(query_client_id, form_client_id) == null) {
//            setErrorMessage("### OAuth2.validateParametersForImplicit() Parameter <client_id> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // redirect_uri: OPTIONAL (for Porcupine: REQUIRED)
//        if(notNull(query_redirect_uri, form_redirect_uri) == null) {
//            setErrorMessage("### OAuth2.validateParametersForImplicit() Parameter <redirect_uri> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // scope: OPTIONAL (for Porcupine: REQUIRED)
//        if(notNull(query_scope, form_scope) == null) {
//            setErrorMessage("### OAuth2.validateParametersForImplicit() Parameter <scope> is missing");
//            throw new InvalidRequestException(this);
//        }
//        
//        // state: RECOMMENDED
//    }
//    
//    public void validateParametersForResourceOwnerPasswordCredentials() 
//                                                throws InvalidRequestException {
//        // grant_type   : REQUIRED
//        if(notNull(query_grant_type, form_grant_type) == null) {
//            setErrorMessage("### OAuth2.validateParametersForResourceOwnerPasswordCredentials() Parameter <grant_type> is missing");
//            throw new InvalidRequestException(this);
//        }    
//        // username: REQUIRED
//        if(notNull(query_username, form_username) == null) {
//            setErrorMessage("### OAuth2.validateParametersForResourceOwnerPasswordCredentials() Parameter <username> is missing");
//            throw new InvalidRequestException(this);
//        }
//        // password: REQUIRED
//        if(notNull(query_password, form_password) == null) {
//            setErrorMessage("### OAuth2.validateParametersForResourceOwnerPasswordCredentials() Parameter <password> is missing");
//            throw new InvalidRequestException(this);
//        }
//        // scope:    OPTIONAL (for Porcupine: REQUIRED)
//        if(notNull(query_scope, form_scope) == null) {
//            setErrorMessage("### OAuth2.validateParametersForResourceOwnerPasswordCredentials() Parameter <scope> is missing");
//            throw new InvalidRequestException(this);
//        }
//    }
//    
//    public void validateParametersForClientCredentials() throws InvalidRequestException {
//        // grant_type : REQUIRED
//        if(notNull(query_grant_type, form_grant_type) == null) {
//            setErrorMessage("### OAuth2.validateParametersForClientCredentials() Parameter <grant_type> is missing");
//            LOG.log(Level.SEVERE, getErrorMessage());
//            throw new InvalidRequestException(this);
//        }
//        
//        // scope      : OPTIONAL (for Porcupine: REQUIRED)
//        if(notNull(query_scope, form_scope) == null) {
//            setErrorMessage("### OAuth2.validateParametersForClientCredentials Parameter <scope> is Missing");
//            throw new InvalidRequestException(this);
//        }
//    }
    
    
//    private Map<String, String> errorParameters() {
//        return errorParameters(null, null, null);
//    }
//    
//    private Map<String, String> errorParameters(String error, String description) {
//        return errorParameters(error, description, null);
//    }
//    
//    private Map<String, String> errorParameters(String error, String description, URI error_uri) {
//        Map<String, String> parameters = new HashMap<String, String>();
//        // Is there a error ?
//        if(error != null) parameters.put(PARAMETER_ERROR, error);
//        // Is there a description ?
//        if(description != null) parameters.put(PARAMETER_ERROR_DESCRIPTION, description);
//        // Is there a error_uri ?
//        if(error_uri != null) parameters.put(PARAMETER_ERROR_URI, error_uri.toString());
//        // Is there a state ?
//        if(state != null) parameters.put(PARAMETER_STATE, state);
//        
//        return parameters;
//    }
    
    public String getClientID() {
        return notNull(query_client_id, form_client_id);
    }
    
    public String getRedirectURI() {
        return notNull(query_redirect_uri, form_redirect_uri);
    }
    
    public String getScope() {
        return notNull(query_scope, form_scope);
    }
    
    public boolean hasState() {
        return getState() != null;
    }
    
    public String getState() {
        return notNull(query_state, form_state);
    }
    
    public String getCode() {
        return notNull(query_code, form_code);
    }
    
    public String getUsername() {
        return notNull(query_username, form_username);
    }
    
    public String getPassword() {
        return notNull(query_password, form_password);
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        LOG.log(Level.SEVERE, errorMessage);
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean isErrorMessage() {
        return getErrorMessage() != null;
    }
    
    public String getAccessToken() {
        return notNull(query_access_token, form_access_token);
    }
    
    public String getRefreshToken() {
        return notNull(query_refresh_token, form_refresh_token);
    }
    
    // UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL 
    //   UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL UTIL 
    private static <T> T notNull(T a, T b) {
        return a != null ? a : b != null ? b : null;
    }
}
