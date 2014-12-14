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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import net.nortlam.porcupine.common.entity.PhoneType;
import net.nortlam.porcupine.common.entity.Role;
import net.nortlam.porcupine.authorization.service.UserService;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("example")
@RequestScoped
public class ExampleController implements Serializable {

    private static final Logger LOG = Logger.getLogger(ExampleController.class.getName());
    
    @EJB
    private UserService userService;
    
    private Role role;
    private String firstName;
    private String lastName;
    
    private PhoneType phoneType;

    public ExampleController() {
    }
    
    public PhoneType[] getPhoneTypes() {
        return PhoneType.values();
    }
    
    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }
    
    public PhoneType getPhoneType() {
        return phoneType;
    }
    
    public Role getRoleUser() {
        try {
            return userService.getRoleUser();
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, "getRoleUser() UNABLE TO FETCH ROLE DATA:{0}", ex.getMessage());
        }
        
        return null;
    }
    
    
    public Role getRoleAdmin() {
        try {
            return userService.getRoleAdmin();
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, "getRoleAdmin() UNABLE TO FETCH ROLE DATA:{0}", ex.getMessage());
        }
        
        return null;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String save() {
        LOG.log(Level.INFO, "save() FirstName:{0} LastName:{1} Role:{2}",
                new Object[] {firstName, lastName, role.getRolename()});
        return "/frontend/user/listuser";
    }
    
    @PostConstruct
    private void init() {
        LOG.log(Level.INFO, "init() <PostConstruct>");
        this.firstName = null;
        this.lastName = null;
        this.role = getRoleUser();
        
    }

}
