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

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletContext;
import net.nortlam.porcupine.common.authenticator.Authenticator;
import net.nortlam.porcupine.common.authenticator.BasicAuthenticator;
import net.nortlam.porcupine.common.authenticator.FormAuthenticator;
import net.nortlam.porcupine.common.exception.AccessDeniedException;
import net.nortlam.porcupine.common.ssl.SSL;
import net.nortlam.porcupine.common.ssl.SSLExchangeCertificate;
import net.nortlam.porcupine.common.ssl.SSLIgnoreCertificate;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class InitParameter implements Serializable {

    private static final Logger LOG = Logger.getLogger(InitParameter.class.getName());

    public InitParameter() {
    }
    
    public static Locale parameterLocale(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_LOCALE);
        return parameter != null ? new Locale(parameter) : Porcupine.DEFAULT_LOCALE;
    }
    
    public static TimeZone parameterTimeZone(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_TIMEZONE);
        return parameter != null ? TimeZone.getTimeZone(parameter) : Porcupine.DEFAULT_TIMEZONE;
    }
    
    public static SimpleDateFormat parameterDateFormat(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_DATE_FORMAT);
        return parameter != null ? new SimpleDateFormat(parameter) : 
                            new SimpleDateFormat(Porcupine.DEFAULT_DATE_FORMAT);
    }
    
    public static String parameterEncryptionAlgorightm(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_ENCRYPTION_ALGORITHM);
        return parameter != null ? parameter : Porcupine.DEFAULT_ENCRYPTION_ALGORIGHTM;
    }
    
    public static String parameterEncoding(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_ENCODING);
        return parameter != null ? parameter : Porcupine.DEFAULT_ENCODING;
    }
    
    public static int parameterAuthorizationCodeExpiration(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_AUTHORIZATION_CODE_EXPIRATION);
        return parameter != null ? Integer.parseInt(parameter) : Porcupine.DEFAULT_AUTHORIZATION_CODE_EXPIRATION;
    }
    
    public static int parameterAccessTokenExpiration(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_ACCESS_TOKEN_EXPIRATION);
        return parameter != null ? Integer.parseInt(parameter) : Porcupine.DEFAULT_ACCESS_TOKEN_EXPIRATION;
    }
    
//    public static String parameterServerScheme(ServletContext context) {
//        String parameter = context.getInitParameter(Porcupine.PARAMETER_SERVER_SCHEME);
//        return parameter != null ? parameter : Porcupine.DEFAULT_SERVER_SCHEME;
//    }
//    
//    public static String parameterHost(ServletContext context) {
//        String parameter = context.getInitParameter(Porcupine.PARAMETER_HOST);
//        return parameter != null ? parameter : Porcupine.DEFAULT_HOST;
//    }
//    
//    public static int parameterPort(ServletContext context) {
//        String parameter = context.getInitParameter(Porcupine.PARAMETER_PORT);
//        return parameter != null ? Integer.parseInt(parameter) : Porcupine.DEFAULT_PORT;
//    }
//    
//    public static String parameterServerContext(ServletContext context) {
//        String parameter = context.getInitParameter(Porcupine.PARAMETER_SERVER_CONTEXT);
//        return parameter != null ? parameter : Porcupine.DEFAULT_SERVER_CONTEXT;
//    }
    
    public static String parameterAuthorizationEndpoint(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_AUTHORIZATION_ENDPOINT);
        return parameter != null ? parameter : Porcupine.DEFAULT_AUTHORIZATION_ENDPOINT;
    }
    
    public static String parameterTokenEndpoint(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_TOKEN_ENDPOINT);
        return parameter != null ? parameter : Porcupine.DEFAULT_TOKEN_ENDPOINT;
    }
    
    public static String parameterCheckEndpoint(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_CHECK_ENDPOINT);
        return parameter != null ? parameter : Porcupine.DEFAULT_CHECK_ENDPOINT;
    }
    
    public static String parameterErrorPage(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_ERROR_PAGE);
        return parameter;
    }
    
//    public static String serverPathWithEndpoint(ServletContext context, String endpoint) {
//        StringBuilder path = new StringBuilder("/")
//                .append(parameterServerContext(context))
//                .append("/").append(endpoint);
//        
//        return path.toString();
//    }
    
    public static URI uriAuthorizeEndpoint(ServletContext context) {
        URI uri = null;
        try {
            String parameter = context.getInitParameter(Porcupine.PARAMETER_AUTHORIZATION_ENDPOINT);
            uri = new URI(parameter != null ? parameter : Porcupine.DEFAULT_AUTHORIZATION_ENDPOINT);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "uriAuthorizeEndpoint() "+
                    "URI SYNTAX EXCEPTION:{0}", ex.getMessage());
        }
        
        return uri;
    }
    
    public static URI uriTokenEndpoint(ServletContext context) {
        URI uri = null;
        try {
            String parameter = context.getInitParameter(Porcupine.PARAMETER_TOKEN_ENDPOINT);
            uri = new URI(parameter != null ? parameter : Porcupine.DEFAULT_TOKEN_ENDPOINT);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "uriTokenEndpoint() "+
                    "URI SYNTAX EXCEPTION:{0}", ex.getMessage());
        }
        
        return uri;
    }
    
    public static URI uriCheckEndpoint(ServletContext context) {
        URI uri = null;
        try {
            String parameter = context.getInitParameter(Porcupine.PARAMETER_CHECK_ENDPOINT);
            uri = new URI(parameter != null ? parameter : Porcupine.DEFAULT_CHECK_ENDPOINT);
        } catch(URISyntaxException ex) {
            LOG.log(Level.SEVERE, "uriAuthorizeEndpoint() "+
                    "URI SYNTAX EXCEPTION:{0}", ex.getMessage());
        }
        
        return uri;
    }

    public static boolean isParameterAuthenticator(ServletContext context) {
        return context.getInitParameter(Porcupine.PARAMETER_AUTHENTICATOR) != null;
    }
    
//    public static Authenticator parameterAuthenticator(URI uri, ServletContext context) 
//                                                            throws AccessDeniedException {
//        String parameterUsername = context.getInitParameter(
//                                    Porcupine.PARAMETER_AUTHENTICATOR_USERNAME);
//        if(parameterUsername == null) {
//            LOG.log(Level.WARNING, "parameterAuthenticator() "+
//                    "Porcupine.AUTHENTICATOR_USERNAME is missing");
//            return null;
//        }
//        
//        String parameterPassword = context.getInitParameter(
//                                    Porcupine.PARAMETER_AUTHENTICATOR_PASSWORD);
//        if(parameterPassword == null) {
//            LOG.log(Level.WARNING, "parameterAuthenticator() "+
//                    "Porcupine.AUTHENTICATOR_PASSWORD is missing");
//            return null;
//        }
//        
//        return parameterAuthenticator(uri, context, parameterUsername, parameterPassword);
//    }
    
    public static Authenticator parameterAuthenticator(URI uri, ServletContext context, 
            String username, String password) throws AccessDeniedException {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_AUTHENTICATOR);
        if(parameter == null) return null; // No Authenticator choose
        
        Authenticator authenticator = null;
        if(parameter.equals(Porcupine.AUTHENTICATOR_FORM))
            authenticator = new FormAuthenticator(uri, username, password);
        else if(parameter.equals(Porcupine.AUTHENTICATOR_BASIC))
            authenticator = new BasicAuthenticator(username, password);
        
        if(authenticator == null) {
            LOG.log(Level.SEVERE, "parameterAuthenticator() Porcupine.AUTHENTICATOR "+
                    " must be Authenticator.FORM or Authenticator.BASIC");
            return null;
        }
        
        return authenticator;
    }
    
    public static String parameterClientID(ServletContext context) {
        return context.getInitParameter(Porcupine.PARAMETER_CLIENT_ID);
    }
    
    public static String parameterClientSecret(ServletContext context) {
        return context.getInitParameter(Porcupine.PARAMETER_CLIENT_SECRET);
    }
    
    public static File parameterStorageTokens(ServletContext context) {
        String directory = context.getInitParameter(Porcupine.PARAMETER_STORAGE_TOKENS);
        
        return directory != null ? new File(directory, Porcupine.DEFAULT_PORCUPINE_TOKENS) : 
                new File(System.getProperty("user.home")
                        .concat(File.separator).concat(Porcupine.DEFAULT_PORCUPINE_TOKENS));
    }
    
    
    // SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL 
    public static String parameterSSLClientKeyStoreFile(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_SSL_CLIENT_KEYSTORE_FILE);
        if(parameter == null) 
            LOG.log(Level.SEVERE, "parameterSSLClientKeyStoreFile() "+
                    "Porcupine.Porcupine.SSL_CLIENT_KEYSTORE_FILE is missing for SSL Connections");
        
        return parameter;
    }
    
    public static String parameterSSLClientKeyStorePassword(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_SSL_CLIENT_KEYSTORE_PASSWORD);
        if(parameter == null)
            LOG.log(Level.SEVERE, "parameterSSLClientKeyStorePassword() "+
                    "Porcupine.Porcupine.SSL_CLIENT_KEYSTORE_PASSWORD is missing for SSL Connections");
        
        return parameter;
    }
    
    public static String parameterSSLClientTrustStoreFile(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_SSL_CLIENT_TRUSTSTORE_FILE);
        if(parameter == null)
            LOG.log(Level.SEVERE, "parameterSSLClientTrustStoreFile() "+
                    "Porcupine.SSL_CLIENT_TRUSTSTORE_FILE is missing for SSL Connections");
        
        return parameter;
    }
    
    public static String parameterSSLClientTrustStorePassword(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_SSL_CLIENT_TRUSTSTORE_PASSWORD);
        if(parameter == null)
            LOG.log(Level.SEVERE, "parameterSSLClientTrustStorePassword() "+
                "Porcupine.SSL_CLIENT_TRUSTSTORE_PASSWORD is missing for SSL Connections");
        
        return parameter;
    }
    
    public static boolean parameterSSLIgnoreCertificate(ServletContext context) {
        String parameter = context.getInitParameter(Porcupine.PARAMETER_SSL_IGNORE_CERTIFICATE);
        if(parameter == null) return false;
        
        return Boolean.valueOf(parameter);
    }
    
    public static SSLContext createContext(ServletContext context) {
        SSL ssl;
        if(parameterSSLIgnoreCertificate(context)) ssl = new SSLIgnoreCertificate();
        else ssl = new SSLExchangeCertificate();
        
        return ssl.keyStoreFile(parameterSSLClientKeyStoreFile(context))
                  .keyStorePassword(parameterSSLClientKeyStorePassword(context))
                  .trustStoreFile(parameterSSLClientTrustStoreFile(context))
                  .trustStorePassword(parameterSSLClientTrustStorePassword(context))
                  .createContext();
    }

}
