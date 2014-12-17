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
package net.nortlam.porcupine.client.token;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.enterprise.inject.Default;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Default @Singleton
public class MemoryClientTokenManagement implements ClientTokenManagement {

    private static final Logger LOG = Logger.getLogger(MemoryClientTokenManagement.class.getName());
    
    private Map<URI, AccessToken> tokens;

    public MemoryClientTokenManagement() {
        tokens = new HashMap<>();
    }
    
    // CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT 
    //  CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT 

    @Override
    public void store(URI resource, AccessToken accessToken) {
        tokens.put(resource, accessToken);
    }

    @Override
    public AccessToken retrieve(URI resource) {
        return tokens.get(resource);
    }
    
    @Override
    public void delete(URI resource) {
        tokens.remove(resource);
    }
}
