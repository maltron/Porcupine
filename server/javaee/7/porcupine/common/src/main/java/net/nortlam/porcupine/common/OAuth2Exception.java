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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mauricio "Maltron" Leal */
public class OAuth2Exception extends Exception {
    
    private static final Logger LOG = Logger.getLogger(OAuth2Exception.class.getName());
    
    private OAuth2 oauth;
    private Map<String,String> parameters = new HashMap<String,String>();

    public OAuth2Exception() {
    }
    
    public OAuth2Exception(OAuth2 oauth) {
        this.oauth = oauth;
    }
    
    public OAuth2Exception(String message) {
        super(message);
    }

    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2Exception(Throwable cause) {
        super(cause);
    }

    public OAuth2Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }
    
    public String buildParameters() {
        StringBuffer result = new StringBuffer();
        for(Map.Entry<String,String> entry: parameters.entrySet()) {
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
            result.append("&");
        }
        
        // Remove the last character
        if(result.length() > 0) result.deleteCharAt(result.length()-1);
        
        return result.toString();
    }
    
    public OAuth2 getOAuth2() {
        return oauth;
    }
    
    public boolean isRedirectURI() {
        if(oauth == null) return false;
        
        return oauth.getRedirectURI() != null;
    }
    
    public URI getRedirectURIwithParameters() {
        URI uri = null;
        try {
            // Current
            URI redirect = URI.create(oauth.getRedirectURI());
            // Added Parameters
            uri = new URI(redirect.getScheme(),
               redirect.getAuthority(), redirect.getPath(),
               buildParameters(), redirect.getFragment());
            
        } catch(URISyntaxException e) {
            LOG.log(Level.SEVERE, "### OAuth2Exception.getRedirectURIwithParameters() URI Syntax:"+e.getMessage());
        }
        
        return uri;
    }
}
