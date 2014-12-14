package net.nortlam.oauth2;

import java.io.Serializable;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class AccessToken implements Serializable {
    
    private String ID;
    private RefreshToken refreshToken;
    
}
