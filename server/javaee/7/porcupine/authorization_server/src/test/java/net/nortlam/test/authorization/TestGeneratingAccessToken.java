package net.nortlam.test.authorization;

import java.io.Serializable;
import java.util.logging.Logger;
import net.nortlam.porcupine.common.IDFactory;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.token.AuthorizationCode;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class TestGeneratingAccessToken implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestGeneratingAccessToken.class.getName());

    @Test
    public void sizeOfAccessToken() {
        String accessToken = IDFactory.getInstance().newAccessToken();
        assertNotNull(accessToken);
        assertEquals(accessToken.length(), AccessToken.LENGTH_TOKEN);
    }
    
    @Test
    public void sizeOfRefreshToken() {
        String refreshToken = IDFactory.getInstance().newRefreshToken();
        assertNotNull(refreshToken);
        assertEquals(refreshToken.length(), AccessToken.LENGTH_REFRESH_TOKEN);
    }
    
    @Test
    public void sizeOfAuthorizationCode() {
        String authorizationCode = IDFactory.getInstance().newAuthorizationCode();
        assertNotNull(authorizationCode);
        assertEquals(authorizationCode.length(), AuthorizationCode.LENGTH_CODE);
                
    }

}
