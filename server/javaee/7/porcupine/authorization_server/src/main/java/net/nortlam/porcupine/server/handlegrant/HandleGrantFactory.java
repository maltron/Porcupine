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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Produces;
import net.nortlam.porcupine.common.Grant;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class HandleGrantFactory implements Serializable {

    private static final Logger LOG = Logger.getLogger(HandleGrantFactory.class.getName());

    private Grant grant;
    private HandleGrantConfiguration config;

    public HandleGrantFactory(Grant grant, HandleGrantConfiguration config) {
        this.grant = grant;
        this.config = config;
    }
    
    @Produces
    public HandleGrant chooseGrant() {
        LOG.log(Level.INFO, "chooseGrant() Grant:{0}", grant);
        
        HandleGrant handleGrant = null;
        switch(grant) {
            case AUTHORIZATION_CODE: 
                handleGrant = new HandleGrantAuthorizationCode(config);
                break;
            case REFRESH_TOKEN:
                handleGrant = new HandleGrantRefreshToken(config);
                break;
            case RESOURCE_OWNER_PASSWORD_CREDENTIALS:
                handleGrant = new HandleGrantResourceOwnerPasswordCredentials(config);
                break;
            case CLIENT_CREDENTIALS:
                handleGrant = new HandleGrantClientCredentials(config);
                break;
        }
        
        return handleGrant;
    }
}
