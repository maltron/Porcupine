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
package temp;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.common.exception.AccessDeniedException;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class FormAuthenticator {
//implements ClientRequestFilter {
//
//    private static final Logger LOG = Logger.getLogger(FormAuthenticator.class.getName());
//    
//    private List<Object> cookies;
//
//    public FormAuthenticator(URI server, String username, String password) throws AccessDeniedException {
//        cookies = new ArrayList<>();
//
//        // It supposed to be the Authenticator's address
//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target(server);
//        
//        Form form = new Form().param("j_username", username).param("j_password", password);
//        
//        Response response = null;
//        try {
//            response = target.path("j_security_check")
//                    .request(MediaType.APPLICATION_FORM_URLENCODED)
//                    .post(Entity.form(form));
//            
//            // PENDING: it must test for success or failure from this authentication
//            
//            // Get all the cookies from the successful authentication
//            for(NewCookie c: response.getCookies().values()) {
//                Cookie cookie = c.toCookie();
//                // Get *ONLY* the JSESSIONID and leave it the rest out
//                if(cookie.getName().equals("JSESSIONID"))
//                    cookies.add(new Cookie("JSESSIONID", cookie.getValue()));
//                else cookies.add(cookie);
//            }
//            
//        } finally {
//            if(response != null) response.close();
//            if(client != null) client.close();
//        }
//    }
//
//    @Override
//    public void filter(ClientRequestContext request) throws IOException {
//        if(cookies != null) request.getHeaders().put("Cookie", cookies);
//    }
}
