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
package net.nortlam.porcupine.resource;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.resource.handlegrant.HandleAuthorizationCodeGrant;
import net.nortlam.porcupine.resource.handlegrant.HandleClientCredentialsGrant;
import net.nortlam.porcupine.resource.handlegrant.HandleGrant;
import net.nortlam.porcupine.resource.handlegrant.HandleImplicitGrant;
import net.nortlam.porcupine.resource.handlegrant.HandleResourceOwnerPasswordCredentialsGrant;
import net.nortlam.porcupine.resource.token.ClientTokenManagement;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Provider
public class SecureRegistration implements DynamicFeature {

    private static final Logger LOG = Logger.getLogger(SecureRegistration.class.getName());

    @Context
    private ServletContext context;
    
    @Inject
    private ClientTokenManagement tokenManagement;
    
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        LOG.log(Level.INFO, "SecureRegistration.configure() Reading Annotation <Secure>");
        Secure secure = resourceInfo.getResourceMethod().getAnnotation(Secure.class);
        if(secure == null) return;
        
        // Try to set which Grant has been choosen
        HandleGrant handleGrant = null;
        switch(secure.grant()) {
            case IMPLICIT: handleGrant = new HandleImplicitGrant(
                                        tokenManagement, secure.scope()); break;
            case RESOURCE_OWNER_PASSWORD_CREDENTIALS: 
                handleGrant = new HandleResourceOwnerPasswordCredentialsGrant(
                        tokenManagement, secure.username(), secure.password(), 
                                                    secure.scope()); break;
            case CLIENT_CREDENTIALS: handleGrant = new HandleClientCredentialsGrant(
                                              tokenManagement, secure.scope()); break;
            // DEFAULT: Authorization Code Grant
            default: tokenManagement.setContext(context); // Setting the context
                handleGrant = new HandleAuthorizationCodeGrant(
                                                tokenManagement, secure.scope());
        }
        
        LOG.log(Level.INFO, "SecureRegistration.configure() Creating new instance of Filter");
        PorcupineFilter filter = new PorcupineFilter(context, handleGrant);
        featureContext.register(filter);
    }
}