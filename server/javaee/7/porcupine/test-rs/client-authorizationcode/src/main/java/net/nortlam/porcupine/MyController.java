package net.nortlam.porcupine;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("my")
@RequestScoped
public class MyController implements Serializable {

    private static final Logger LOG = Logger.getLogger(MyController.class.getName());
    
    private String email;

    public MyController() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
    public void requestEmail(ActionEvent event) {
        // Step #1: Request an Access Token
        // Step #2: Request the Resource with the Access Token
        
    }

}
