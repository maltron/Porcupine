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
package net.nortlam.porcupine.common;

import java.io.Serializable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.common.authenticator.BasicAuthenticator;
import org.junit.Test;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class TestAuthenticator implements Serializable {

    @Test
    public void test() {
        BasicAuthenticator authenticator = new BasicAuthenticator("maltron@gmail.com", "maltron");
        Client client = ClientBuilder.newClient().register(authenticator);
        WebTarget target = client.target("http://localhost:8080/server/oauth2/authorize");
        
        Response response = null;
        try {
            response = target.request().get();
            System.out.printf("Response %d %s\n",response.getStatus(), response.readEntity(String.class));
            
        } finally {
            if(response != null) response.close(); client.close();
        }
    }

}
