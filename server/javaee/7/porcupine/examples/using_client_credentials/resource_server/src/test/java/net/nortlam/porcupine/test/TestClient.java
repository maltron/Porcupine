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
package net.nortlam.porcupine.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.examples.Person;
import org.junit.Test;

public class TestClient {
    
    @Test
    public void testClient() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/testcc/rest/resource");
        Response response = null;
        try {
            response = target.request(MediaType.APPLICATION_XML)
                    .post(Entity.entity(Person.class, MediaType.WILDCARD_TYPE));
            System.out.printf("%d %s %s\n",response.getStatus(), response.getStatusInfo(),
                    response.hasEntity() ? response.readEntity(String.class) : "");
            
        } finally {
            if(response != null) response.close(); client.close();
        }
    }
    
}
