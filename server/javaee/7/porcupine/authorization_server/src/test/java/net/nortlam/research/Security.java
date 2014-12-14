package net.nortlam.research;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.jboss.crypto.CryptoUtil;
import org.junit.Test;
/**
 *
 * @author Mauricio "Maltron" Leal */
public class Security implements Serializable {


    @Test
    public void encrypt() {
        String algorithm = "SHA-256";
        String encoding = "base64";
        System.out.println(">>> Encrypt(god):"+encrypt(algorithm, encoding, "god"));
        System.out.println(">>> Encrypt Simple (god):"+encryptSimple("god"));
        System.out.println(">>> Encrypt(admin):"+encrypt(algorithm, encoding, "admin"));
        System.out.println(">>> Encrypt Simple (admin):"+encryptSimple("admin"));
        System.out.println(">>> Encrypt(maltron):"+encrypt(algorithm, encoding, "maltron"));
        System.out.println(">>> Encrypt Simple (maltron):"+encryptSimple("maltron"));
    }    
    
    public String encrypt(String algorithm, String encoding, String password) {
        try {
//            byte[] hash = MessageDigest.getInstance(algorithm).digest(clearTextPassword.getBytes());
            return CryptoUtil.createPasswordHash(algorithm, encoding, "utf-8", null, password);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
//        try {
//            byte[] bytepassword = password.getBytes("utf-8");
//            
//            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//            messageDigest.update(bytepassword);
//            byte[] hash = messageDigest.digest();
//            return new String(Base64.getEncoder().encode(hash));
//            
//        } catch(UnsupportedEncodingException ex) {
//            System.err.printf("### UNSUPPORTED ENCONDING %s\n", ex.getMessage());
//        } catch(NoSuchAlgorithmException ex) {
//            System.err.printf("### NO SUCH ALGORIRITHM %s\n", ex.getMessage());
//        }
        
        return null;
    }
    
    public String encryptSimple(String password) {
        try {
            byte[] bytepassword = password.getBytes("utf-8");
            
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytepassword);
            byte[] hash = messageDigest.digest();
            return new String(Base64.getEncoder().encode(hash));
            
        } catch(UnsupportedEncodingException ex) {
            System.err.printf("### UNSUPPORTED ENCONDING %s\n", ex.getMessage());
        } catch(NoSuchAlgorithmException ex) {
            System.err.printf("### NO SUCH ALGORIRITHM %s\n", ex.getMessage());
        }
        
        return null;
    }
    
    
//   public static String createPasswordHash(String hashAlgorithm, String hashEncoding,
//      String hashCharset, String username, String password, DigestCallback callback)
//   {
//      byte[] passBytes;
//      String passwordHash = null;
//
//      // convert password to byte data
//      try
//      {
//         if(hashCharset == null)
//            passBytes = password.getBytes();
//         else
//            passBytes = password.getBytes(hashCharset);
//      }
//      catch(UnsupportedEncodingException uee)
//      {
//         log.error("charset " + hashCharset + " not found. Using platform default.", uee);
//         passBytes = password.getBytes();
//      }
//
//      // calculate the hash and apply the encoding.
//      try
//      {
//         MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
//         if( callback != null )
//            callback.preDigest(md);
//         md.update(passBytes);
//         if( callback != null )
//            callback.postDigest(md);
//         byte[] hash = md.digest();
//         if(hashEncoding.equalsIgnoreCase(BASE64_ENCODING))
//         {
//            passwordHash =  encodeBase64(hash);
//         }
//         else if(hashEncoding.equalsIgnoreCase(BASE16_ENCODING))
//         {
//            passwordHash =  encodeBase16(hash);
//         }
//         else if(hashEncoding.equalsIgnoreCase(RFC2617_ENCODING))
//         {
//            passwordHash =  encodeRFC2617(hash);
//         }
//         else
//         {
//            log.error("Unsupported hash encoding format " + hashEncoding);
//         }
//      }
//      catch(Exception e)
//      {
//         log.error("Password hash calculation failed ", e);
//      }
//      return passwordHash;
//   }    
}
