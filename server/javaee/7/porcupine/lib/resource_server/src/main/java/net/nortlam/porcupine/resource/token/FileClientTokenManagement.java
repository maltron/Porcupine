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
package net.nortlam.porcupine.resource.token;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Default;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.token.AuthorizationCode;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Default
public class FileClientTokenManagement implements ClientTokenManagement {

    private static final Logger LOG = Logger.getLogger(FileClientTokenManagement.class.getName());
    
    public static final String FILENAME_AUTHORIZATION_CODE = "ac-";
    public static final String FILENAME_ACCESS_TOKEN = "at-";
    public static final String FILENAME_REFRESH_TOKEN = "rt-";
    public static final String SEPARATOR = "-";
    public static final String FILENAME_ENDING_XML = ".xml";
    
    private ServletContext context;

    public FileClientTokenManagement() {
    }
    
    @Override
    public void setContext(ServletContext context) {
        this.context = context;
    }
    
    // DIRECTORY DIRECTORY DIRECTORY DIRECTORY DIRECTORY DIRECTORY DIRECTORY 
    private java.io.File getDirectory() {
        return InitParameter.parameterStorageTokens(context);
    }
    
    // AUTHORIZATION CODE AUTHORIZATION CODE AUTHORIZATION CODE AUTHORIZATION CODE 
    private String getFilenameForAuthorizationCode(AuthorizationCode code) {
        return getFilenameForAuthorizationCode(code.getCode());
    }
    
    private String getFilenameForAuthorizationCode(String code) {
        StringBuilder builder = new StringBuilder(FILENAME_AUTHORIZATION_CODE);
        builder.append(SEPARATOR);
        builder.append(code);
        builder.append(FILENAME_ENDING_XML);
        
        return builder.toString();
    }
    
    private java.io.File fileAuthorizationCode(AuthorizationCode code) {
        return new java.io.File(getFilenameForAuthorizationCode(code));
    }
    
    private java.io.File fileAuthorizationCode(String code) {
        return new java.io.File(getFilenameForAuthorizationCode(code));
    }
    
    // ACCESS TOKEN ACCESS TOKEN ACCESS TOKEN ACCESS TOKEN ACCESS TOKEN ACCESS TOKEN 
    private String getFilenameForAccessToken(AccessToken token) {
        return getFilenameForAccessToken(token.getToken());
    }
    
    private String getFilenameForAccessToken(String accessToken) {
        StringBuilder builder = new StringBuilder(FILENAME_ACCESS_TOKEN);
        builder.append(SEPARATOR);
        builder.append(accessToken);
        builder.append(FILENAME_ENDING_XML);
        
        return builder.toString();
    }
    
    private java.io.File fileAccessToken(AccessToken accessToken) {
        return new java.io.File(getFilenameForAccessToken(accessToken));
    }
    
    private java.io.File fileAccessToken(String accessToken) {
        return new java.io.File(getFilenameForAccessToken(accessToken));
    }
    
    // REFRESH TOKEN REFRESH TOKEN REFRESH TOKEN REFRESH TOKEN REFRESH TOKEN 
    private String getFilenameForRefreshToken(AccessToken accessToken) {
        return getFilenameForRefreshToken(accessToken.getRefreshToken());
    }
    
    private String getFilenameForRefreshToken(String refreshToken) {
        StringBuilder builder = new StringBuilder(FILENAME_REFRESH_TOKEN);
        builder.append(SEPARATOR);
        builder.append(refreshToken);
        builder.append(FILENAME_ENDING_XML);
        
        return builder.toString();
    }
    
    private java.io.File fileRefreshToken(AccessToken accessToken) {
        return new java.io.File(getFilenameForRefreshToken(accessToken));
    }
    
    private java.io.File fileRefreshToken(String refreshToken) {
        return new java.io.File(getFilenameForRefreshToken(refreshToken));
    }
    
    // CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT 
    //  CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT CLIENT TOKEN MANAGEMENT 
//    @Override
//    public void store(AuthorizationCode authorizationCode) throws IOException {
//        try (FileOutputStream output = new FileOutputStream(
//                        getFilenameForAuthorizationCode(authorizationCode))) {
//            try {
//                JAXBContext jaxbcontext = JAXBContext.newInstance(AuthorizationCode.class);
//                Marshaller marshaller = jaxbcontext.createMarshaller();
//                marshaller.marshal(authorizationCode, output);
//                
//            } catch(JAXBException ex) {
//                LOG.log(Level.SEVERE, "FileClientTokenManagement.store(AuthorizationCode) "+
//                        "Unable to store Authorization Code XML File:{0}", ex.getMessage());
//            }
//        }
//    }
//
//    @Override
//    public AuthorizationCode retrieveAuthorizationCode(String code) throws IOException {
//        AuthorizationCode authorizationCode = null;
//        
//        try(FileInputStream input = new FileInputStream(
//                                    getFilenameForAuthorizationCode(code))) {
//            try {
//                JAXBContext context = JAXBContext.newInstance(AuthorizationCode.class);
//                authorizationCode = (AuthorizationCode)context
//                                            .createUnmarshaller().unmarshal(input);
//            } catch(JAXBException ex) {
//                LOG.log(Level.SEVERE, "FileClientTokenManagement.retrieveAuthorizationCode() "+
//                        " Unable to handle AuthorizationCode XML: {0}", ex.getMessage());
//            }
//        }
//        
//        return authorizationCode;
//    }
//
//    @Override
//    public void delete(AuthorizationCode authorizationCode) throws IOException {
//        java.io.File file = fileAuthorizationCode(getFilenameForAuthorizationCode(SEPARATOR));
//        if(file.exists()) file.delete();
//        else LOG.log(Level.WARNING, "FileClientTokenManagement.delete() Unable to locale file:{0}", file.toPath());
//    }

    @Override
    public void store(AccessToken accessToken) throws IOException {
        // Everytime we're going to store an Access Token, 2 different files
        // will be generated: one for AccessToken and anoter for RefreshToken
        
        // Step #1/2: Storing AccessToken
        java.io.File directory = getDirectory(); directory.mkdirs();
        java.io.File fileAccessToken = new java.io.File(directory, 
                                    getFilenameForAccessToken(accessToken));
        try (FileOutputStream output = new FileOutputStream(fileAccessToken)) {
            try {
                JAXBContext jaxbcontext = JAXBContext.newInstance(AccessToken.class);
                Marshaller marshaller = jaxbcontext.createMarshaller();
                marshaller.marshal(accessToken, output);
                
            } catch(JAXBException ex) {
                LOG.log(Level.SEVERE, "FileClientTokenManagement.store(AccessToken) "+
                        "Unable to store AccessToken Code XML File:{0}", 
                                                        ex.getMessage());
            }
        }
        
        // Step #2/2: Storing RefreshToken (The file will be empty)
        if(accessToken.hasRefreshToken()) {
            java.io.File fileRefreshToken = new java.io.File(directory, 
                        getFilenameForRefreshToken(accessToken));
            try (FileOutputStream output = new FileOutputStream(fileRefreshToken)) {
                // Nothing to do
            }
        }
    }

    @Override
    public AccessToken retrieveAccessToken(String token) throws IOException {
        AccessToken accessToken = null;
        
        java.io.File directory = getDirectory();
        java.io.File fileAccessToken = new java.io.File(directory, 
                                            getFilenameForAccessToken(token));
        try(FileInputStream input = new FileInputStream(fileAccessToken)) {
            JAXBContext jaxbcontext = JAXBContext.newInstance(AccessToken.class);
            accessToken = (AccessToken)jaxbcontext.createUnmarshaller().unmarshal(input);
            
        } catch(JAXBException ex) {
            LOG.log(Level.SEVERE, "FileClientTokenManagement.retrieve(AccessToken) "+
                    " Unable to retrieve AccessToken XML File:{0}", ex.getMessage());
        }
        
        return accessToken;
    }
    
    @Override
    public void delete(AccessToken accessToken) throws IOException {
        String refreshToken = accessToken.getRefreshToken();

        java.io.File directory = getDirectory();
        // Delete the Access Token File
        java.io.File fileAccessToken = new java.io.File(directory, 
                getFilenameForAccessToken(accessToken));
        if(fileAccessToken.exists()) fileAccessToken.delete();
        
        java.io.File fileRefreshToken = new java.io.File(directory, 
                getFilenameForRefreshToken(accessToken));
        if(fileRefreshToken.exists()) fileRefreshToken.delete();
    }
}
