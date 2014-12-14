package net.nortlam.oauth2;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class TokenFactory {
    
    private static TokenFactory instance;
    
    public static TokenFactory getInstance() {
        if(instance == null) instance = new TokenFactory();
        
        return instance;
    }
    
    

}
