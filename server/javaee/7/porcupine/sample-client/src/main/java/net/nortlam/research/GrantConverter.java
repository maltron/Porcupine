package net.nortlam.research;

import java.util.logging.Logger;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import net.nortlam.porcupine.common.Grant;

/**
 *
 * @author Mauricio "Maltron" Leal */
//@Provider
public class GrantConverter implements ParamConverter<Grant> {

    private static final Logger LOG = Logger.getLogger(GrantConverter.class.getName());

    @Override
    public Grant fromString(String value) {
        LOG.info(">>> GrantConverter.fromString():"+value);
        return Grant.valueOf(value);
//        
//        throw new IllegalArgumentException("Invalid Grant:"+value);
    }

    @Override
    public String toString(Grant value) {
        LOG.info(">>> GrantConverter.toString():"+value.toString());
        
        return value.toString();
    }
}
