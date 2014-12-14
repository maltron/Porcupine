package net.nortlam.encrypt;

import java.security.MessageDigest;
import java.util.Base64;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.jboss.crypto.CryptoUtil;


public class EncryptPassword {
    
    public EncryptPassword() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void encrypt() {
        String algorithm = "SHA-512";
        String encoding = "base64";
        String clearTextPassword = "admin";
        String hashedPassword = null;
        
        try {
//            byte[] hash = MessageDigest.getInstance(algorithm).digest(clearTextPassword.getBytes());
            hashedPassword = CryptoUtil.createPasswordHash(algorithm, encoding, null, null, clearTextPassword);
            System.out.println(">>> Clear Text Password:"+clearTextPassword);
            System.out.println(">>> Encrypted Password:"+hashedPassword);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
