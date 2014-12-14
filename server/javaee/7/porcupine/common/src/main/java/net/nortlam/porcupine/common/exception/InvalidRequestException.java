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

import java.util.logging.Logger;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.OAuth2Exception;
/**
 *
 * @author Mauricio "Maltron" Leal */
public class InvalidRequestException extends OAuth2Exception {

    private static final Logger LOG = Logger.getLogger(InvalidRequestException.class.getName());
    
    public InvalidRequestException(OAuth2 oauth) {
        super(oauth);
        // State: Always add the state if present (according to specification)
        if(oauth.hasState()) addParameter(OAuth2.PARAMETER_STATE, oauth.getState());
        
        addParameter(OAuth2.PARAMETER_ERROR, OAuth2.PARAMETER_ERROR_INVALID_REQUEST);
        if(oauth.isErrorMessage())
            addParameter(OAuth2.PARAMETER_ERROR_DESCRIPTION, oauth.getErrorMessage());
    }
}
