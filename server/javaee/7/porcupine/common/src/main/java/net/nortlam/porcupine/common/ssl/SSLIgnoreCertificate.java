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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class SSLIgnoreCertificate implements SSL, X509TrustManager {

    private static final Logger LOG = Logger.getLogger(SSLIgnoreCertificate.class.getName());
    
    private String securityProtocol;

    public SSLIgnoreCertificate() {
        securityProtocol = DEFAULT_SECURITY_PROTOCOL;
    }
    
    // X509 TRUST MANAGER X509 TRUST MANAGER X509 TRUST MANAGER X509 TRUST MANAGER 
    //  X509 TRUST MANAGER X509 TRUST MANAGER X509 TRUST MANAGER X509 TRUST MANAGER 

    @Override
    public void checkClientTrusted(X509Certificate[] xcs, String string) 
                                                    throws CertificateException {
        // Nothing to do
    }

    @Override
    public void checkServerTrusted(X509Certificate[] xcs, String string) 
                                                        throws CertificateException {
        // Nothing to do
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null; // Nothing to return
    }
    
    // SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL 
    //   SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL SSL 

    @Override
    public SSL securityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
        
        return this;
    }
    
    @Override
    public SSL keyStoreType(String keyStoreType) {
        return this;
    }
    
    @Override
    public SSL keyStoreFile(String keyStoreFile) {
        return this;
    }
    
    @Override
    public SSL keyStorePassword(String keyStorePassword) {
        return this;
    }
    
    @Override
    public SSL trustStoreType(String trustStoreType) {
        return this;
    }
    
    @Override
    public SSL trustStoreFile(String trustStoreFile) {
        return this;
    }
    
    @Override
    public SSL trustStorePassword(String trustStorePassword) {
        return this;
    }

    @Override
    public SSLContext createContext() {
        try {
            SSLContext context = SSLContext.getInstance(securityProtocol); // NoSuchAlgorithmException
            context.init(null, new TrustManager[] {this}, null); // KeyManagementException 
        
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
