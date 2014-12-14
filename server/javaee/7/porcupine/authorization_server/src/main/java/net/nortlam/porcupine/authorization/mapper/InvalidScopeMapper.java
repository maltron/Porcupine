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
package net.nortlam.porcupine.authorization.mapper;

import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.PorcupineErrorMessage;
import net.nortlam.porcupine.common.exception.InvalidScopeException;

/**
 * Section 4.1.2.1
 * 
 * The requested scope is invalid, unknown, or malformed.
 *
 * @author Mauricio "Maltron" Leal */
@Provider
public class InvalidScopeMapper implements ExceptionMapper<InvalidScopeException> {

    private static final Logger LOG = Logger.getLogger(InvalidScopeMapper.class.getName());

    @Override
    public Response toResponse(InvalidScopeException e) {
        // There is a Redirection URI. Use it !!!
        if(e.isRedirectURI())
            return Response.seeOther(e.getRedirectURIwithParameters()).build();
        
        // Nothing to be redirect to. Use Porcupine specific
        PorcupineErrorMessage errorMessage = new PorcupineErrorMessage(
        OAuth2.PARAMETER_ERROR_INVALID_SCOPE, 
        e.buildParameters(), 
        e.getOAuth2().getErrorMessage());

        return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
    }

}
