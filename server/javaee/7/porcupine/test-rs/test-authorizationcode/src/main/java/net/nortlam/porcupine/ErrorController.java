package net.nortlam.porcupine;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import net.nortlam.porcupine.common.OAuth2;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("error")
@RequestScoped
public class ErrorController implements Serializable {

    private static final Logger LOG = Logger.getLogger(ErrorController.class.getName());
    
    private String error;
    private String errorDescription;
    private String errorURI;
    private String state;

    public ErrorController() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
    
    public String getOficialErrorDescription() {
        if(error == null) return null;
        else if(error.equals(OAuth2.PARAMETER_ERROR_INVALID_REQUEST))
            return OAuth2.PARAMETER_DESCRIPTION_INVALID_REQUEST;
        else if(error.equals(OAuth2.PARAMETER_ERROR_INVALID_SCOPE))
            return OAuth2.PARAMETER_DESCRIPTION_INVALID_SCOPE;
        else if(error.equals(OAuth2.PARAMETER_ERROR_UNAUTHORIZED_CLIENT))
            return OAuth2.PARAMETER_DESCRIPTION_UNAUTHORIZED_CLIENT;
        else if(error.equals(OAuth2.PARAMETER_ERROR_UNSUPPORTED_RESPONSE_TYPE))
            return OAuth2.PARAMETER_DESCRIPTION_UNSUPPORTED_RESPONSE_TYPE;
        else if(error.equals(OAuth2.PARAMETER_ERROR_SERVER_ERROR))
            return OAuth2.PARAMETER_DESCRIPTION_SERVER_ERROR;
        else if(error.equals(OAuth2.PARAMETER_ERROR_ACCESS_DENIED))
            return OAuth2.PARAMETER_DESCRIPTION_ACCESS_DENIED;
        else if(error.equals(OAuth2.PARAMETER_ERROR_TEMPORARILY_UNAVAILABLE))
            return OAuth2.PARAMETER_ERROR_DESCRIPTION_TEMPORARILY_UNAVAILABLE;
        
        return null;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorURI() {
        return errorURI;
    }

    public void setErrorURI(String errorURI) {
        this.errorURI = errorURI;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
