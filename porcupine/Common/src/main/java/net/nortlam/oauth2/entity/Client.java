package net.nortlam.oauth2.entity;

import net.nortlam.oauth2.util.URLBuilder;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class Client implements URLBuilder {
    
    private String ID;
    private String name;
    private String description;
    
    // URLBuilder URLBuilder URLBuilder URLBuilder URLBuilder URLBuilder URLBuilder 
    //   URLBuilder URLBuilder URLBuilder URLBuilder URLBuilder URLBuilder URLBuilder 
    
    /**
     * OAuth 2.0: 4.1.1  Authorization Request
     * 
     * GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
     *   &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
     * Host: server.example.com
     * 
     * 
     */
    @Override
    public String toURL() {
        return null;
    }
}
