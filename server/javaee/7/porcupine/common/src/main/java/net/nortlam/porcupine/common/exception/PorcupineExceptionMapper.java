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
package net.nortlam.porcupine.common.exception;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.common.PorcupineException;
/**
 *
 * @author Mauricio "Maltron" Leal */
@Provider
public class PorcupineExceptionMapper implements ExceptionMapper<PorcupineException> {

    private static final Logger LOG = Logger.getLogger(PorcupineExceptionMapper.class.getName());

    @Override
    public Response toResponse(PorcupineException exception) {
        // Is there any parameters ?
        StringBuilder params = new StringBuilder();
        Map<String, String> errorParameters = exception.getErrorParameters();
        if(errorParameters != null) {
            for(Map.Entry<String,String> entry: errorParameters.entrySet()) {
                params.append(entry.getKey());
                params.append("=");
                params.append(entry.getValue());
                params.append("&");
            }
            
            // Remove the last "&"
            params.deleteCharAt(params.length()-1);
        }
        
        LOG.info(">>> Parameters:"+params.toString());
        
        URI client = null; 
        try {
            client = new URI("http", null, "localhost", 8080, "/client", params.toString(), null);
            LOG.info(">>> URI:"+client.toString());
        } catch(URISyntaxException e) {
            LOG.severe("### PorcupineExceptionMapper:"+e.getMessage());
        }
        ResponseBuilder builder = Response.seeOther(client);
        return builder.build();
    }
}
