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
package net.nortlam.porcupine.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A simple way to reconstruct URI's 
 * 
 * @author Mauricio "Maltron" Leal */
public class URIBuilder {

    private static final Logger LOG = Logger.getLogger(URIBuilder.class.getName());
    
    public static String buildParameters(String ... value) {
        if(value == null) return null;
        
        if(value.length % 2 != 0) {
            LOG.log(Level.SEVERE, "### URIBuilder.buildParameters() Number of Parameters are *not* even");
            return null;
        }
        
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < value.length; i += 2) {
            // Only add parameters which are *NOT* null
            // (This can be tricky to trace problems in the future
            if(value[i] == null || value[i+1] == null) continue;
            
            builder.append(value[i]);
            builder.append("=");
            // PENDING: THIS ONE SHOULD BE ENCODED
            builder.append(value[i+1]); 
            builder.append("&");
        }
        
        // Remove the last character &
        builder.deleteCharAt(builder.length()-1);
        
        return builder.toString();
    }
    
    public static String buildParameters(MultivaluedMap<String, String> values) {
        LOG.log(Level.INFO, "buildParameters(MultivaluedMap)");
        StringBuilder builderParams = new StringBuilder();
        
        StringBuilder buildervalues = new StringBuilder();
        for(Entry<String, List<String>> entry: values.entrySet()) {
            buildervalues.delete(0, buildervalues.length());
            int size = entry.getValue().size();
            for(int i=0; i < size; i++)
                buildervalues.append(entry.getValue().get(i)).append(i < size -1 ? "," : "");
            
            builderParams.append(entry.getKey());
            builderParams.append("=");
            // PENDING: THIS ONE SHOULD BE ENCODED
            builderParams.append(buildervalues.toString());
            builderParams.append("&");
        }
        
        return builderParams.toString();
    }
    
    public static URI buildURI(String uri) {
        return buildURI(uri, null);
    }
    
    public static URI buildURI(String uri, String ... parameters) {
        URI realURI = null;
        try {
            realURI = new URI(uri);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "### URIBuilder.buildURI() URI SYNTAX:{0}", ex.getMessage());
            LOG.log(Level.SEVERE, "### URIBuilder.buildURI() Parameter URI:{0}", uri);
        }
        
        return buildURI(realURI.getScheme(), realURI.getHost(), 
                                realURI.getPort(), realURI.getPath(), parameters);
    }
    
    public static URI buildURI(String scheme, String host, int port, String path) {
        return buildURI(scheme, host, port, path, null);
    }
    
    public static URI buildURI(URI uri, String ... parameters) {
        return buildURI(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), parameters);
    }
    
    public static URI buildURI(String scheme, String host, int port, String path, String ... parameters) {
        URI uri = null;
        try {
            uri = new URI(scheme, null, host, port, path, buildParameters(parameters), null);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "### URIBuilder.buildURI() URI SYNTAX:{0}", ex.getMessage());
            LOG.log(Level.SEVERE, "### URIBuilder.buildURI() Scheme:{0} Host:{1} Port:{2} Path:{3}", 
                    new Object[] {scheme, host, port, path});
        }
        
        return uri;
    }
    
    public static URI buildURIMultivaluedMap(String uri, MultivaluedMap<String, String> parameters) {
        URI realURI = null;
        try {
            realURI = new URI(uri);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "### URIBuilder.buildURIMultivaluedMap() URI SYNTAX:{0}",
                    ex.getMessage());
        }
        
        return buildURIMultivaluedMap(realURI.getScheme(), realURI.getHost(), 
                                realURI.getPort(), realURI.getPath(), parameters);
    }

    public static URI buildURIMultivaluedMap(String scheme, String host, int port, 
                        String path, MultivaluedMap<String, String> parameters) {
        URI uri = null;
        try {
            uri = new URI(scheme, null, host, port, path, buildParameters(parameters), null);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "### URIBuilder.buildURI() URI SYNTAX:{0}", ex.getMessage());
            LOG.log(Level.SEVERE, "### URIBuilder.buildURI() Scheme:{0} Host:{1} Port:{2} Path:{3}", 
                    new Object[] {scheme, host, port, path});
        }
        
        return uri;
    }
    
}
