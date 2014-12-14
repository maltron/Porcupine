package net.nortlam.oauth2.entity;

import java.util.UUID;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class IDFactory {
    
    private static IDFactory instance;
    
    public static IDFactory getInstance() {
        if(instance == null) instance = new IDFactory();
               
        return instance;
    }
    
    public String newID() {
        return UUID.randomUUID().toString();
    }
}
