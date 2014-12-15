package net.nortlam.porcupine;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("accessToken")
@SessionScoped
public class AccessTokenController implements Serializable {

    private static final Logger LOG = Logger.getLogger(AccessTokenController.class.getName());
    
    private String acccessToken;
    private String refreshToken;
    private long expiration;

    public AccessTokenController() {
    }

    public String getAcccessToken() {
        return acccessToken;
    }

    public void setAcccessToken(String acccessToken) {
        this.acccessToken = acccessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
