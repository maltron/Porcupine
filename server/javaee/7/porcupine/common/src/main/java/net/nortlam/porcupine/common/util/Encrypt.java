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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import net.nortlam.porcupine.common.InitParameter;
/**
 *
 * @author Mauricio "Maltron" Leal */
public class Encrypt implements Serializable {
    
    private static final Logger LOG = Logger.getLogger(Encrypt.class.getName());

    public Encrypt() {
    }
    
    public static String encrypt(ServletContext context, String password) {
        return encrypt(InitParameter.parameterEncryptionAlgorightm(context), 
                InitParameter.parameterEncoding(context), password);
    }
    
    public static String encrypt(String encryptionAlgorithm, String encoding, 
                                                            String password) {
        try {
            // Default: SHA-256
            MessageDigest digest = MessageDigest.getInstance(encryptionAlgorithm);
            // Default: UTF-8
            digest.update(password.getBytes(encoding));
            
            return new String(Base64.getEncoder().encode(digest.digest()));
            
        } catch(NoSuchAlgorithmException ex) {
            // No supposed to happen
            LOG.log(Level.SEVERE, "### encrypt() NO SUCH ALGORITHM:{0}", ex.getMessage());
        } catch(UnsupportedEncodingException ex) {
            // No suppoed to happen
            LOG.log(Level.SEVERE, "### encrypt() UNSUPPORTED ENCONDING:{0}",
                    ex.getMessage());
        }
        
        return null;
    }

}
