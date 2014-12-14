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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.common.exception.AccessDeniedException;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Form
public class FormAuthenticator implements Authenticator {
    
    public static final String FORM_SUBMIT = "j_security_check";
    public static final String FIELD_USERNAME = "j_username";
    public static final String FIELD_PASSWORD = "j_password";
    
    public static final String COOKIE_JSESSION_ID = "JSESSIONID";

    private static final Logger LOG = Logger.getLogger(FormAuthenticator.class.getName());
    
    // A Cookie repositories. This must contain a JSESSIONID which it has identified
    // the user to further access pages
    private List<Object> cookies;
    
    public FormAuthenticator() {
    }

    public FormAuthenticator(URI uri, String username, String password) throws AccessDeniedException {
        if(uri == null) {
            LOG.log(Level.SEVERE, "FormAuthenticator() Missing Parameter URI");
            throw new AccessDeniedException();
        }
        
        if(username == null) {
            LOG.log(Level.SEVERE, "FormAuthenticator() Missing Parameter username");
            throw new AccessDeniedException();
        }
        
        if(password == null) {
            LOG.log(Level.SEVERE, "FormAuthenticator() Missing Parameter password");
            throw new AccessDeniedException();
        }
        
        cookies = new ArrayList<>();
        
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        
        javax.ws.rs.core.Form form = new javax.ws.rs.core.Form();
        form.param(FIELD_USERNAME, username);
        form.param(FIELD_PASSWORD, password);
        
        Response response = null;
        try {
            response = target.path(FORM_SUBMIT).request(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(form));
//            LOG.log(Level.INFO, ">>> FormAuthenticator() {0} {1}",
//                    new Object[] {response.getStatus(), response.getStatusInfo()});
//            if(response.hasEntity())
//                LOG.log(Level.INFO, ">>> FormAuthenticator() {0}", response.readEntity(String.class));
                    
            
            boolean found = false; // Indicates if a JSESSIONID was found
            
            for(NewCookie c: response.getCookies().values()) {
                Cookie cookie = c.toCookie();
                // Get *ONLY* the JSESSIONID and leave it the rest out
                if(cookie.getName().equals(COOKIE_JSESSION_ID)) {
                    found = true;
                    cookies.add(new Cookie(COOKIE_JSESSION_ID, cookie.getValue()));
                } else cookies.add(cookie);
            }
            
            // PENDING: A clever way must exist to check if the authentication
            //          was valid or not. 
            if(!found) {
                LOG.log(Level.SEVERE, "No JSESSIONID was found");
                throw new AccessDeniedException();
            }
        
        } finally {
            if(response != null) response.close();  client.close();
        }
    }
    
    // CLIENT REQUEST FILTER CLIENT REQUEST FILTER CLIENT REQUEST FILTER 
    //   CLIENT REQUEST FILTER CLIENT REQUEST FILTER CLIENT REQUEST FILTER 
    @Override
    public void filter(ClientRequestContext request) throws IOException {
        if(cookies != null) request.getHeaders().put("Cookie", cookies);
    }
}
