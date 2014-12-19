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
package net.nortlam.porcupine.examples;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.nortlam.porcupine.common.Grant;
import net.nortlam.porcupine.resource.Secure;

/**
 * @author Mauricio "Maltron" Leal */
@Path("/resource")
public class Resource implements Serializable {

    private static final Logger LOG = Logger.getLogger(Resource.class.getName());

    public Resource() {
    }
    
    @POST @Secure(grant = Grant.RESOURCE_OWNER_PASSWORD_CREDENTIALS,
            scope="DOCUMENTS", username = "hello@hello.com", password = "123")
    @Produces(MediaType.APPLICATION_JSON)
    public Document getDocument() {
        return new Document("1-type-B-RHJ", "Hello World");
    }


}