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
package net.nortlam.porcupine.authorization.converter;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import net.nortlam.porcupine.common.entity.Phone;
import net.nortlam.porcupine.common.Porcupine;

/**
 *
 * @author Mauricio "Maltron" Leal */
@FacesConverter(forClass = Phone.class)
public class PhoneConverter implements Converter {
    
//    public static final String ID = "net.nortlam.porcupine.authorization.phone";
    public static final String SEPARATOR_TYPE = ":";
    public static final String SEPARATOR_START_AREA_CODE = "(";
    public static final String SEPARATOR_END_AREA_CODE = ")";

    private static final Logger LOG = Logger.getLogger(PhoneConverter.class.getName());
    
    // CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER 
    //   CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER 

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
//        LOG.log(Level.INFO, "getAsObject():{0}", value);
        if(value == null) {
            LOG.log(Level.WARNING, "getAsObject() Value is *NULL*");
            return null;
        }

        ResourceBundle bundle = context.getApplication()
                .getResourceBundle(context, Porcupine.DEFAULT_RESOUCE_BUNDLE);
        
        String branchname = bundle.getString("branch");
        return new Phone(value, branchname);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
//        LOG.log(Level.INFO, ">>> getAsString():{0}", value);
        if(value == null) {
            LOG.log(Level.WARNING, "getAsString() Value is *NULL*");
            return null;
        }
        
        if(!(value instanceof Phone)) {
            LOG.log(Level.WARNING, "getAsString() Value is *not* a instance of Phone");
            return null;
        }
        
//        UIViewRoot viewRoot = context.getViewRoot();
//        Locale locale = viewRoot.getLocale();
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        ResourceBundle bundle = ResourceBundle.getBundle("message", locale, loader);
        ResourceBundle bundle = context.getApplication()
                .getResourceBundle(context, Porcupine.DEFAULT_RESOUCE_BUNDLE);
        
        String branchname = bundle.getString("branch");
        return ((Phone)value).toString(branchname);
    }        
}
