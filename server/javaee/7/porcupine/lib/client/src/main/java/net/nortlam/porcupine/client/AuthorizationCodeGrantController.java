/*
 * 
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
 * 
 */
package net.nortlam.porcupine.client;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.client.token.ClientTokenManagement;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 *
 * @author Mauricio "Maltron" Leal */
public abstract class AuthorizationCodeGrantController<T> 
                extends AbstractPorcupineController implements FetchResource<T> {

    private static final Logger LOG = Logger.getLogger(AuthorizationCodeGrantController.class.getName());
    
    @Inject
    protected ClientTokenManagement tokenManagement;
    
    private Client client;
    private String authorizationCode;

    public AuthorizationCodeGrantController() {
    }

    @Override
    public abstract String getPassword();
    
    @Override
    public abstract String getUsername();
    
    @Override
    public abstract String getScope();
    
    @Override
    public abstract String getRedirectURI();
    
    public void setCode(String code) {
        this.authorizationCode = code;
        if(code == null) return;
        
        LOG.log(Level.INFO, "setCode() {0}", authorizationCode);
        
        // Good, if we've got the code, we're half way there
        // Requesting an Access token then
        LOG.log(Level.INFO, "setCode() Requesting a new Access Token");
        AccessToken accessToken = requestAccessToken(Grant.AUTHORIZATION_CODE, 
                                                            authorizationCode);
        
        LOG.log(Level.INFO, "setCode() Sucessfull acquired. Storing");
        tokenManagement.store(getResource(), accessToken);
        
        // Requesting the Resource intented
        requestResource();
    }
    
    public String getCode() {
        return authorizationCode;
    }
    
    protected void requestResource() {
        LOG.log(Level.INFO, "requestResource()");
        
        URI resource = getResource();
        
        // Is there any Token available ?
        LOG.log(Level.INFO, "requestResource() Is there any Acesss Tokens"+
                " available for:{0}", resource);
        AccessToken accessToken = tokenManagement.retrieve(resource);
        if(accessToken != null) {
            LOG.log(Level.INFO, "requestResource() There is a Token Available");
            LOG.log(Level.INFO, "requestResource() NOW:{0} Token:{1}", 
                    new Object[] {debug(), debug(accessToken.getExpiration())});
            if(!accessToken.isExpired(getContext())) {
                LOG.log(Level.INFO, "requestResource() Token is still Valid."+
                        " Performing Fetch on desired Resource");
                
                // GOOD TO GO
                performFetchResource();
                return;
                
            } else {
                // REFRESHING TOKEN // REFRESHING TOKEN // REFRESHING TOKEN 
                //  // REFRESHING TOKEN // REFRESHING TOKEN // REFRESHING TOKEN 
                LOG.log(Level.WARNING, "requestResource() Token is *EXPIRED*."+
                        " Deleting the old accessToken.");
                tokenManagement.delete(resource);
                LOG.log(Level.WARNING, "requestResource() Refreshing Tokens.");
                accessToken = requestAccessToken(Grant.REFRESH_TOKEN, getScope(), accessToken);
                // If something wrong happens, it will redirect to Error Page
                if(accessToken != null) {
                    LOG.log(Level.INFO, "requestResource() Refreshing successfull.");
                    tokenManagement.store(resource, accessToken);
                } else {
                    LOG.log(Level.SEVERE, "requestResource() Refreshing failed");
                }
                requestResource(); // Try another time, this time with a new Token
                return;
            }
            
            
        }
        
        // There isn't any Token Avaible. Redirect to Authorization Server
        LOG.log(Level.INFO, "requestResource() NO TOKENS FOUND. Redirect "+
                "to Authorization Server");
        redirectAuthorizationServer(Grant.AUTHORIZATION_CODE);
    }
    
//    public T requestResource() {
//        LOG.log(Level.INFO, "requestEmail()");
//        
//        // Is there any AccessToken avaliable ?
//        LOG.log(Level.INFO, "requestEmail() Is there any Access Tokens available for:{0}", getResource());
//        AccessToken accessToken = tokenManagement.retrieve(getResource());
//        if(accessToken != null) {
//            LOG.log(Level.INFO, "requestEmail() Access Token:{0}", accessToken.getToken());
//            LOG.log(Level.INFO, "requestEmail() NOW:{0} EXPIRATION:{1}",
//            new Object[] {debugExpiration(), debugExpiration(accessToken.getExpiration())});
//            if(!accessToken.isExpired(getContext())) {
//                this.authorizationCode = null; // No need for a Authorization Code anymore
//                LOG.log(Level.INFO, "requestEmail() Yes, there is and it still valid. Fetch it");
//                // There is an Access Token avaliable. Fetch Resource
//                return fetchResource();
//                
//            } else { // Access Token is EXPIRED
//                LOG.log(Level.INFO, "requestEmail() Access Token is expired. Refreshing");
//                // PENDING: Refreshing 
//                accessToken = requestAccessToken(Grant.REFRESH_TOKEN, getScope(), accessToken);
//                tokenManagement.store(getResource(), accessToken);
//                
//                LOG.log(Level.INFO, "requestEmail() Requesting the same content again");
//                return fetchResource();
//            }
//        }
//        
//        // No, there isn't any Access Token avaliable yet. 
//        // Redirect to Authorize Server in order to get one
//        LOG.log(Level.INFO, "requestEmail() No, Redirecting to Authorization Server");
//        redirectAuthorizationServer(Grant.AUTHORIZATION_CODE);
//    }
    
    private void performFetchResource() {
        URI resource = getResource();
        Response response = getResponse();
        try {
            int code = response.getStatus();
            T result = response.readEntity(typeParameterClass());
            
            this.authorizationCode = null; // No need, once the response it's OK
            if(code == Response.Status.OK.getStatusCode()) {
                setSuccess(result);
            } else {
                LOG.log(Level.SEVERE, "performFetchResource() FAILURE:{0} {1}",
                        new Object[]{code, result});
                setFailture(result);
            }
            
        } finally {
            if(response != null) response.close();
            if(this.client != null) this.client.close();
        }
    }
    
    protected WebTarget getWebTarget() {
        URI resource = getResource();
        this.client = clientInstance(resource);
        return this.client.target(resource);
    }
    
    protected String getTokenAsBearer() {
        URI resource = getResource();
        AccessToken token = tokenManagement.retrieve(resource);
        return token.toStringAuthorizationBearer();
    }
    
    // FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 
    //  FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE FETCH RESOURCE 
    @Override
    public abstract URI getResource();

    @Override
    public abstract Class<T> typeParameterClass();
    
    @Override
    public abstract Response getResponse();
    
    @Override
    public abstract void setSuccess(T t);
    
    @Override
    public abstract void setFailture(T t);
}
