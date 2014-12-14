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
package net.nortlam.porcupine.client.token;

import java.io.IOException;
import javax.servlet.ServletContext;
import net.nortlam.porcupine.common.token.AccessToken;

/**
 *
 * @author Mauricio "Maltron" Leal */
public interface ClientTokenManagement {
    
//    public void store(AuthorizationCode authorizationCode) throws IOException;
//    public AuthorizationCode retrieveAuthorizationCode(String code) throws IOException;
//    public void delete(AuthorizationCode authorizationCode) throws IOException;
    
    public void setContext(ServletContext context);
    
    public void store(AccessToken accessToken) throws IOException;
    public AccessToken retrieveAccessToken(String accessToken) throws IOException;
    public void delete(AccessToken accessToken) throws IOException;
}
