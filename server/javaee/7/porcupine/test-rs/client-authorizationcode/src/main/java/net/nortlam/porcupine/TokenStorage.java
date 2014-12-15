package net.nortlam.porcupine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import net.nortlam.porcupine.common.token.AccessToken;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Singleton
public class TokenStorage implements Serializable {

    private static final Logger LOG = Logger.getLogger(TokenStorage.class.getName());

    private Map<String, AccessToken> tokens;

    public TokenStorage() {
    }
    
    @PostConstruct
    private void initController() {
        tokens = new HashMap<>();
    }
    
    public void put(String resource, AccessToken accessToken) {
        tokens.put(resource, accessToken);
    }
    
    public AccessToken get(String resource) {
        return tokens.get(resource);
    }
}
