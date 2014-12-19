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

import java.io.Serializable;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nortlam.porcupine.client.exception.UnableToFetchResourceException;
import net.nortlam.porcupine.client.exception.UnableToObtainAccessTokenException;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 * WARNING: This Grant has NO ERROR PAGE
 *
 * @author Mauricio "Maltron" Leal */
public abstract class ClientCredentialsController<T> 
                            extends AbstractPorcupineController<T> 
                                        implements Serializable, FetchResource<T> {

    private static final Logger LOG = Logger.getLogger(ClientCredentialsController.class.getName());

    public ClientCredentialsController() {
    }

    public ClientCredentialsController(Class<T> typeParameterClass) {
        setTypeParameterClass(typeParameterClass);
    }

    @Override
    public String getUsername() { // NOT USED for this Grant
        return null;
    }
    
    @Override
    public String getPassword() { // NOT USED for this Grant
        return null;
    }
    
    @Override
    public abstract String getScope(); // REQUIRED
    
    @Override 
    public String getRedirectURI() { // NOT USED for this Grant
        return null;
    }

    @Override
    protected void requestResource() 
    throws UnableToObtainAccessTokenException, UnableToFetchResourceException {
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
            }
        }
        
        // There isn't any Token Avaible. Redirect to Authorization Server
        LOG.log(Level.INFO, "requestResource() NO TOKENS FOUND. Requesting a new Token");
        accessToken = requestAccessToken(Grant.CLIENT_CREDENTIALS, getScope(), null);
        // If something goes wrong, it will generate an Exception
        LOG.log(Level.INFO, "requestResource() Token:{0}", accessToken != null ?
            accessToken.getToken() : "NULL");
        tokenManagement.store(resource, accessToken);
            
        // WARNING: RECURSION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        requestResource(); // WARNING: RECURSION !!!!!!!!!!!!!!!!!!!!!!!!!
    }
}
