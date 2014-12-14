/**
 * Copyright 2014 Mauricio "Maltron" Leal <maltron@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nortlam.porcupine.authorization.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.nortlam.porcupine.common.Porcupine;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("login")
@ViewScoped
public class LoginController extends AbstractController implements Serializable {

    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());
    
    private String originalURL;
    private String email;
    private String password;

    public LoginController() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void login() {
        ExternalContext external = getExternal();
        HttpServletRequest request = getRequest();
        
        if(request == null) {
            LOG.log(Level.SEVERE, "login() UNABLE TO OBTAIN HttpServletRequest instance");
            fatal("data.error", "please.contact.administrator");
            
            return;
        }
        
        // Step #1/2: Login it
        try {
            request.login(email, password);
        } catch(ServletException ex) {
            LOG.log(Level.SEVERE, "login() Access Denied: Email:{0} Password:{1}",
                    new Object[] {email, password});
            error("access.denied");
            return;
        }
        
        // Step #2/2: Redirect to the original URL
        try {
            LOG.log(Level.INFO, "login() Default URL:{0}", external
                    .getApplicationContextPath().concat(Porcupine.DEFAULT_URL));
            
            String redirect = originalURL != null ? originalURL :
                    external.getApplicationContextPath().concat(Porcupine.DEFAULT_URL);
            external.redirect(redirect);
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "login() UNABLE TO REDIRECT:{0}", ex.getMessage());
        }
    }
    
    public void logout() {
        try {
            getRequest().logout();
        } catch(ServletException ex) {
            LOG.log(Level.SEVERE, "logout() Unable to perform logout:{0}", ex.getMessage());
        }
//        ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
//        ((HttpServletRequest)external.getRequest()).logout();
    }
    
    @PostConstruct
    private void init() {
        // The URL requested, before entering login page
        originalURL = getOriginalURL();
        LOG.log(Level.INFO, "init() Original URL:{0}", originalURL);
    }
}
