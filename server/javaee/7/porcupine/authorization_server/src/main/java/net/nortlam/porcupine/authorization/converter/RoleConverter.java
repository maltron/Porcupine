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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.xml.bind.JAXBException;
import net.nortlam.porcupine.common.entity.Role;

/**
 *
 * @author Mauricio "Maltron" Leal */
@FacesConverter(forClass = Role.class)
public class RoleConverter implements Converter {

    private static final Logger LOG = Logger.getLogger(RoleConverter.class.getName());
    private static final String SEPARATOR = ":";

    // CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER 
    //  CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER CONVERTER 
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value == null) return null;

        Role role = new Role();
        try {
            role.fromXML(value);
        } catch(JAXBException ex) {
            LOG.log(Level.SEVERE, "getAsObject() Unable to parse XML Content to Role object");
            return null;
        }
        
        return role;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null) {
            LOG.log(Level.WARNING, "getAsString() Value is *NULL*");
            return null;
        }
        
        if(!(value instanceof Role)) {
            LOG.log(Level.WARNING, "getAsString() Value *not* a instance of Role");
            return null;
        }
        
        Role role = (Role)value;
        try {
            return role.toXML();
        } catch(JAXBException ex) {
            LOG.log(Level.SEVERE, "getAsString() Unable to marshall content into XML");
        }
        
        return null;
    }
}
