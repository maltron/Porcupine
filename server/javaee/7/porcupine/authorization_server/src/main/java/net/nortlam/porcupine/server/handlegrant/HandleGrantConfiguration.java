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
package net.nortlam.porcupine.server.handlegrant;

import javax.servlet.ServletContext;
import net.nortlam.porcupine.authorization.service.ClientService;
import net.nortlam.porcupine.authorization.token.TokenManagement;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.entity.User;

/**
 * All the objects needed for each Grant
 *
 * @author Mauricio "Maltron" Leal */
public interface HandleGrantConfiguration {
    
    public OAuth2 getOAuth();
    public ServletContext getContext();
    public ClientService getClientService();
    public TokenManagement getTokenManagement();
    public User getPrincipal();

}
