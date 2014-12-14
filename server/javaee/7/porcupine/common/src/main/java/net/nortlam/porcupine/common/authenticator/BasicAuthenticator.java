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
package net.nortlam.porcupine.common.authenticator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Basic
public class BasicAuthenticator implements Authenticator {

    private static final Logger LOG = Logger.getLogger(BasicAuthenticator.class.getName());
    
    public static final String AUTHORIZATION_BASIC = "Basic";
    
    private String username;
    private String password;

    public BasicAuthenticator() {
    }
    
    public BasicAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    private String getBasicAuthentication() {
        try {
        return String.format("%s %s", AUTHORIZATION_BASIC,
            DatatypeConverter.printBase64Binary(
                String.format("%s:%s", this.username, this.password)
                                                    .getBytes("UTF-8")));
        } catch(UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unable to encode into UTF-8");
        }
    }
    
    // CLIENT REQUEST FILTER CLIENT REQUEST FILTER CLIENT REQUEST FILTER 
    //   CLIENT REQUEST FILTER CLIENT REQUEST FILTER CLIENT REQUEST FILTER 
    @Override
    public void filter(ClientRequestContext request) throws IOException {
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, getBasicAuthentication());
    }
}
