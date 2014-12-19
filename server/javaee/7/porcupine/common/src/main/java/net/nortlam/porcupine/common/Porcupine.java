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

/*
 * Specifics for the Porcupine Implementation */
package net.nortlam.porcupine.common;

import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class Porcupine {
    
    // Default subdirectory for storing tokens
    public static final String DEFAULT_PORCUPINE_TOKENS = ".porcupine";
    
    // Default web page
    public static final String DEFAULT_URL = "/faces/index.xhtml";
    
    // Resource Bundle Name Variable
    public static final String DEFAULT_RESOUCE_BUNDLE = "message";
    
    // Priorities
    public static final int OAUTH2_CHECK = 1000;
    public static final int AUTHORIZATION_CODE = 2000;
    public static final int BEARER_CHECK = 3000;
    
//    // Date Format (used mostly on expirations)
//    // PENDING: Add this to properties file
//    // PENDING: Add some TimeZone issues
//    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss.S");
    
    // PARAMETERS PARAMETERS PARAMETERS PARAMETERS PARAMETERS PARAMETERS PARAMETERS 
    //  PARAMETERS PARAMETERS PARAMETERS PARAMETERS PARAMETERS PARAMETERS PARAMETERS 
    public static final String PARAMETER_LOCALE = "Porcupine.LOCALE";
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    
    public static final String PARAMETER_TIMEZONE = "Porcupine.TIMEZONE";
    public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
    
    public static final String PARAMETER_DATE_FORMAT = "Porcupine.DATEFORMAT";
    public static final String DEFAULT_DATE_FORMAT = "dd/MMM/yyyy HH:mm:ss.S";
    
    public static final String PARAMETER_ENCRYPTION_ALGORITHM = "Porcupine.ENCRYPTION_ALGORITHM";
    public static final String DEFAULT_ENCRYPTION_ALGORIGHTM = "SHA-256";
    
    public static final String PARAMETER_ENCODING = "Porcupine.ENCODING";
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    public static final String PARAMETER_AUTHORIZATION_CODE_EXPIRATION = "Porcupine.AUTHORIZATION_CODE_EXPIRATION";
    public static final int DEFAULT_AUTHORIZATION_CODE_EXPIRATION = 1000*60*10; // 10 minute
    
    public static final String PARAMETER_ACCESS_TOKEN_EXPIRATION = "Porcupine.ACCESS_TOKEN_EXPIRATION";
    public static final int DEFAULT_ACCESS_TOKEN_EXPIRATION = 1000*60*5; // 5 minutes
    
    public static final String PARAMETER_SERVER_SCHEME = "Porcupine.SERVER_SCHEME";
    public static final String DEFAULT_SERVER_SCHEME = "http";
    
    public static final String PARAMETER_HOST = "Porcupine.HOST";
    public static final String DEFAULT_HOST = "localhost";
    
    public static final String PARAMETER_PORT = "Porcupine.PORT";
    public static final int DEFAULT_PORT = 8080;
    
    public static final String PARAMETER_SERVER_CONTEXT = "Porcupine.SERVER_CONTEXT";
    public static final String DEFAULT_SERVER_CONTEXT = "server";
    
    public static final String PARAMETER_AUTHORIZATION_ENDPOINT = "Porcupine.AUTHORIZATION_ENDPOINT";
    public static final String DEFAULT_AUTHORIZATION_ENDPOINT = "oauth2/authorize";
    
    public static final String PARAMETER_TOKEN_ENDPOINT = "Porcupine.TOKEN_ENDPOINT";
    public static final String DEFAULT_TOKEN_ENDPOINT = "oauth2/token";

    public static final String PARAMETER_CHECK_ENDPOINT = "Porcupine.CHECK_ENDPOINT";
    public static final String DEFAULT_CHECK_ENDPOINT = "oauth2/check";
    
    public static final String PARAMETER_ERROR_PAGE = "Porcupine.ERROR_PAGE";
    
    public static final String PARAMETER_AUTHENTICATOR = "Porcupine.AUTHENTICATOR";
    public static final String AUTHENTICATOR_FORM = "Authenticator.FORM";
    public static final String AUTHENTICATOR_BASIC = "Authenticator.BASIC";
    
//    public static final String PARAMETER_AUTHENTICATOR_USERNAME = "Porcupine.AUTHENTICATOR_USERNAME";
//    
//    public static final String PARAMETER_AUTHENTICATOR_PASSWORD = "Porcupine.AUTHENTICATOR_PASSWORD";
    
    public static final String PARAMETER_CLIENT_ID = "Porcupine.CLIENT_ID";
    public static final String PARAMETER_CLIENT_SECRET = "Porcupine.CLIENT_SECRET";

    // Only used for Authorization Code Grant type
    public static final String PARAMETER_STORAGE_TOKENS = "Porcupine.STORAGE_TOKENS";
}
