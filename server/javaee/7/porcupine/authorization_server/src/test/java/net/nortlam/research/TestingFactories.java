package net.nortlam.research;

import java.io.Serializable;
import net.nortlam.porcupine.common.IDFactory;
import org.junit.Test;
/**
 *
 * @author Mauricio "Maltron" Leal */
public class TestingFactories implements Serializable {


    @Test
    public void generateClientID() {
        IDFactory factory = IDFactory.getInstance();
        System.out.printf(">>> ClientID:%s Secret:%s\n", 
                factory.newClientID(), factory.newClientSecret());
    }
    
    @Test
    public void generateRefreshToken() {
        IDFactory factory = IDFactory.getInstance();
        String accessToken = factory.newAccessToken();
        String refreshToken = factory.newRefreshToken();
        String authorizationCode = factory.newAuthorizationCode();
        
        System.out.printf(">>> AccessToken[%d]:%s\n", 
                                            accessToken.length(), accessToken);
        System.out.printf(">>> RefreshToken[%d]:%s\n", 
                                            refreshToken.length(), refreshToken);
        System.out.printf(">>> AuthorizationCode[%d]:%s\n", 
                                 authorizationCode.length(), authorizationCode);
        
    }
    
}
