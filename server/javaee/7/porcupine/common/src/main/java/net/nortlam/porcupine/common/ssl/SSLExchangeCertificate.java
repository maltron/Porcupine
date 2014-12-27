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
package net.nortlam.porcupine.common.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Default;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Default
public class SSLExchangeCertificate implements SSL {

    private static final Logger LOG = Logger.getLogger(SSLExchangeCertificate.class.getName());
    
    private String securityProtocol;
    private String keyStoreString, trustStoreString;
    private char[] keyStorePassword, trustStorePassword;
    private String keyStoreType, trustStoreType;
    
    private String keyManagerFactoryAlgorithm, trustManagerFactoryAlgorithm;

    public SSLExchangeCertificate() {
        securityProtocol = DEFAULT_SECURITY_PROTOCOL;
        keyStoreType = KeyStore.getDefaultType();
        trustStoreType = KeyStore.getDefaultType();
        
        keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    }
    
    @Override
    public SSL securityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
        
        return this;
    }
    
    @Override
    public SSL keyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        
        return this;
    }
    
    @Override
    public SSL keyStoreFile(String keyStoreFile) {
        this.keyStoreString = keyStoreFile;
        
        return this;
    }
    
    @Override
    public SSL keyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword.toCharArray();
        
        return this;
    }
    
    @Override
    public SSL trustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
        
        return this;
    }
    
    @Override
    public SSL trustStoreFile(String trustStoreFile) {
        this.trustStoreString = trustStoreFile;
        
        return this;
    }
    
    @Override
    public SSL trustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword.toCharArray();
        
        return this;
    }

    // SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL 
    //   SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL 

    @Override
    public SSLContext createContext() {
        
        // 111111111111111111111111111111111111111111111111111111111111111111111
        LOG.log(Level.INFO, "createContext() Step #1: Loading KeyStore {0}"+
                " Using Type:{1}, Using Password:{2}",
                new Object[] {keyStoreString, keyStoreType, keyStorePassword});
        
        KeyStore keyStore = null;
        try {
            try(InputStream inputKeyStore = new FileInputStream(keyStoreString);) { // FileNotFoundException
                keyStore = KeyStore.getInstance(keyStoreType); // KeyStoreException
                keyStore.load(inputKeyStore, keyStorePassword);
                
            } // AutoClose
            
        } catch(FileNotFoundException ex) {
            String errorMessage = String.format("### createContext() "+
                    "File:%s FILE NOT FOUND EXCEPTION:%s", 
                    keyStoreString, ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(KeyStoreException ex) {
            String errorMessage = String.format("### createContext() "+
                    "KeyStore Type:%s KEY STORE EXCEPTION:%s", 
                    keyStoreType, ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(IOException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error loading key store from file. IO EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(NoSuchAlgorithmException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error initializing key store (algorithm to check key store integrity not found). "+
                    "NO SUCH ALGORITHM EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
//        } catch(NoSuchProviderException ex) {
//            String errorMessage = String.format("### createContext() "+
//                    "Error initializing key Store (provider not registered) "+
//                    "NO SUCH ALGORITHM EXCEPTION:%s", 
//                    ex.getMessage());
//            LOG.log(Level.SEVERE, errorMessage);
//            throw new IllegalStateException(errorMessage, ex);
        } catch(CertificateException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Cannot load key store certificates "+
                    "CERTIFICATE EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        }
        
        // 222222222222222222222222222222222222222222222222222222222222222222222
        LOG.log(Level.INFO, "createContext() Step #2: Loading KeyManagerFactory"+
        " Using Algorithm:{0}, Using Password:{1}",
        new Object[] {keyManagerFactoryAlgorithm, keyStorePassword});

        KeyManagerFactory keyManagerFactory = null;
        try {
            keyManagerFactory = KeyManagerFactory.getInstance(keyManagerFactoryAlgorithm);
            keyManagerFactory.init(keyStore, keyStorePassword);
            
        } catch(UnrecoverableKeyException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error initializing key manager factory (unrecoberable key) "+
                    "UNRECOVERABLE KEY EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(KeyStoreException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error initializing key manager factory (operation failed) "+
                    "KEY STORE EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(NoSuchAlgorithmException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error initializing key manager factory (algorithm not supported) "+
                    "NO SUCH ALGORITHM EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        }
        
        // 333333333333333333333333333333333333333333333333333333333333333333333
        LOG.log(Level.INFO, "createContext() Step #3: Loading TrustStore {0}"+
                " Using Type:{1}, Using Password:{2}",
                new Object[] {trustStoreString, trustStoreType, trustStorePassword});
        
        KeyStore trustStore = null;
        try {
            try(InputStream inputTrustStore = new FileInputStream(trustStoreString)) {
                trustStore = KeyStore.getInstance(trustStoreType);
                trustStore.load(inputTrustStore, trustStorePassword);
            }
            
        } catch(FileNotFoundException ex) {
            String errorMessage = String.format("### createContext() "+
                    "File:%s FILE NOT FOUND EXCEPTION:%s", 
                    trustStoreString, ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(KeyStoreException ex) {
            String errorMessage = String.format("### createContext() "+
                    "KeyStore Type:%s KEY STORE EXCEPTION:%s", 
                    trustStoreType, ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(IOException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error loading key store from file. IO EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(NoSuchAlgorithmException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error initializing key store (algorithm to check key store integrity not found). "+
                    "NO SUCH ALGORITHM EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(CertificateException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Cannot load key store certificates "+
                    "CERTIFICATE EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        }
        
        // 444444444444444444444444444444444444444444444444444444444444444444444
        LOG.log(Level.INFO, "createContext() Step #4: Loading TrustManagerFactory {0}"+
                " Using Algorithm:{1}, Using Password:{2}",
                new Object[] {trustManagerFactoryAlgorithm, trustStorePassword});
        
        TrustManagerFactory trustManagerFactory = null;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(trustManagerFactoryAlgorithm);
            trustManagerFactory.init(trustStore);
            
        } catch(KeyStoreException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error initializing trust manager factory (operation failed) "+
                    "KEY STORE EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(NoSuchAlgorithmException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Error initializing trust manager factory (algorithm not supported) "+
                    "NO SUCH ALGORITHM EXCEPTION:%s", 
                    ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        }

        // 555555555555555555555555555555555555555555555555555555555555555555555
        LOG.log(Level.INFO, "createContext() Step #5: Initializing SSLContext"+
                " Using Security Protocol:{0}",
                new Object[] {securityProtocol});
        
        try {
            SSLContext context = SSLContext.getInstance(securityProtocol); // NoSuchAlgorithmException
            context.init(
                    keyManagerFactory != null ? keyManagerFactory.getKeyManagers() : null,
                    trustManagerFactory != null ? trustManagerFactory.getTrustManagers() : null,
                    null);
        
            return context;
        
        } catch(NoSuchAlgorithmException ex) {
            String errorMessage = String.format("### createContext() "+
                    "Algorithm:%s NO SUCH ALGORITHM EXCEPTION:%s", 
                    securityProtocol, ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        } catch(KeyManagementException ex) {
            String errorMessage = String.format("### createContext() "+
                    "KEY MANAGEMENT EXCEPTION:%s", ex.getMessage());
            LOG.log(Level.SEVERE, errorMessage);
            throw new IllegalStateException(errorMessage, ex);
        }
    }
    
}
