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

import java.util.Base64;
import java.util.UUID;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class IDFactory {
    
    public static final String PORCUPINE_CLIENT = "porcupine-client";
    public static final String PORCUPINE_CLIENT_SECRET = "porcupine-client-secret";
    public static final String PORCUPINE_AUTHORIZATION_CODE = "porcupine-authorization-code";
    
    public static final String PORCUPINE_ACCESS_TOKEN = "porcupine-access-token";
    public static final String PORCUPINE_REFRESH_TOKEN = "porcupine-refresh-token";
    
    public static final String PORCUPINE_SEPARATOR = "-";
    
    public static final Base64.Encoder ENCODER = Base64.getEncoder();
    public static final Base64.Decoder DECODER = Base64.getDecoder();
    
    private static IDFactory instance;
    
    public static IDFactory getInstance() {
        if(instance == null) instance = new IDFactory();
        
        return instance;
    }
    
    
    public String newClientID() {
        StringBuilder builder = new StringBuilder(PORCUPINE_CLIENT);
        builder.append(PORCUPINE_SEPARATOR);
        builder.append(UUID.randomUUID().toString());
        
        
        return ENCODER.encodeToString(builder.toString().getBytes());
    }
    
    public String newClientSecret() {
        StringBuilder builder = new StringBuilder(PORCUPINE_CLIENT_SECRET);
        builder.append(PORCUPINE_SEPARATOR);
        builder.append(UUID.randomUUID().toString());
        
        return ENCODER.encodeToString(builder.toString().getBytes());
    }
    
    public String newAuthorizationCode() {
        StringBuilder builder = new StringBuilder(PORCUPINE_AUTHORIZATION_CODE);
        builder.append(PORCUPINE_SEPARATOR);
        builder.append(UUID.randomUUID().toString());
        
        return ENCODER.encodeToString(builder.toString().getBytes());
    }
    
    public String newAccessToken() {
        StringBuilder builder = new StringBuilder(PORCUPINE_ACCESS_TOKEN);
        builder.append(PORCUPINE_SEPARATOR);
        builder.append(UUID.randomUUID().toString());
        
        return ENCODER.encodeToString(builder.toString().getBytes());
    }

    public String newRefreshToken() {
        StringBuilder builder = new StringBuilder(PORCUPINE_REFRESH_TOKEN);
        builder.append(PORCUPINE_SEPARATOR);
        builder.append(UUID.randomUUID().toString());
        
        return ENCODER.encodeToString(builder.toString().getBytes());
    }

}
