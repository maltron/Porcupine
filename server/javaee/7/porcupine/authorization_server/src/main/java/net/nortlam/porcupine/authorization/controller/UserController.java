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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import net.nortlam.porcupine.common.entity.Phone;
import net.nortlam.porcupine.common.entity.PhoneType;
import net.nortlam.porcupine.common.entity.Role;
import net.nortlam.porcupine.common.entity.User;
import net.nortlam.porcupine.authorization.service.UserService;
import net.nortlam.porcupine.common.util.Encrypt;

/**
 *
 * @author Mauricio "Maltron" Leal
 */
@Named("user")
@ViewScoped
public class UserController extends AbstractController implements Serializable {

    private static final Logger LOG = Logger.getLogger(UserController.class.getName());

    @EJB
    private UserService userService;
    
    @Context
    private ServletContext context;

    private long ID;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String passwordCheck;

    // Change Password
    private String currentPassword;
    private String newPassword;
    private String newPasswordCheck;

    // Phone
    private PhoneType phoneType;
    private String areaCode;
    private String number;
    private String branch;

    private Role role;

    private PhoneType phoneTypeSelected;

    private User selected;

    public UserController() {
    }

    public void newUser(ActionEvent event) {
        setID(0); // Indicates a new User must be created

        email = null;
        firstName = null;
        lastName = null;
        password = null;
        passwordCheck = null;

        currentPassword = null;
        newPassword = null;
        newPasswordCheck = null;

        phoneType = PhoneType.MOBILE;
        phoneTypeSelected = PhoneType.MOBILE;
        areaCode = null;
        number = null;
        branch = null;

        role = getRoleUser();
    }

    public boolean isNew() {
        return getID() == 0;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public PhoneType getPhoneTypeSelected() {
        return phoneTypeSelected;
    }

    public void setPhoneTypeSelected(PhoneType phoneTypeSelected) {
        this.phoneTypeSelected = phoneTypeSelected;
    }

    public PhoneType[] getPhoneTypes() {
        return PhoneType.values();
    }

    public Role getRole() {
        LOG.log(Level.INFO, "getRole():{0}", role);
        return role;
    }

    public void setRole(Role role) {
        LOG.log(Level.INFO, "setRole():{0}", role);
        this.role = role;
    }

    public Role getRoleUser() {
        return userService.getRoleUser();
    }

    public Role getRoleAdmin() {
        return userService.getRoleAdmin();
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordCheck() {
        return newPasswordCheck;
    }

    public void setNewPasswordCheck(String newPasswordCheck) {
        this.newPasswordCheck = newPasswordCheck;
    }

    public User getSelected() {
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    public List<User> getUsers() {
        return userService.findAll();
    }

    public int getCount() {
        return userService.count();
    }

    public void editUser(ActionEvent event) {
        if (selected == null) {
            LOG.log(Level.SEVERE, "editUser() there isn't any User selected");
            throw new AbortProcessingException("editUser() there isn't any User selected");
        }

        this.ID = selected != null ? selected.getID() : 0;
        this.email = selected != null ? selected.getEmail() : null;
        this.firstName = selected != null ? selected.getFirstName() : null;
        this.lastName = selected != null ? selected.getLastName() : null;
        this.password = null;
        this.passwordCheck = null;

        Phone phone = selected != null ? selected.getPhone() : null;
        this.phoneType = phone != null ? phone.getPhoneType() : PhoneType.MOBILE;
        this.areaCode = phone != null ? phone.getAreaCode() : null;
        this.number = phone != null ? phone.getNumber() : null;
        this.branch = phone != null ? phone.getBranch() : null;

        this.role = selected != null ? selected.getRole() : getRoleUser();
    }

    public void saveUser(ActionEvent event) {
        // Is there any number at all ?
        if (areaCode == null && number == null && branch == null) {
            phoneTypeSelected = null;
        }
        Phone phone = new Phone(phoneTypeSelected, areaCode, number, branch);

        // Check if both passwords match
        if (password != null && passwordCheck != null
                && !(password.equals(passwordCheck))) {
            error("passwords.both.doesnt.match", null, "messages messagepassword");
            throw new AbortProcessingException("Both passwords doesn't match");
        }
        
        // Password must be encyrpted
        password = Encrypt.encrypt(context, password);

        if (isNew()) { // CREATED !!!!

            // Check if this user already exist
            if (userService.alreadyExistEmail(email)) {
                error("email.exists");
                throw new AbortProcessingException("Email already exists");
            }

            // Check if there is a combination of first and last name
            if (userService.alreadyExistName(firstName, lastName)) {
                error("combination.first.last.exist");
                throw new AbortProcessingException("First and Last Name already exits");
            }

            userService.create(email, password, firstName, lastName, phone, role);

        } else { // UPDATED !!!!
            User existing = userService.read(getID());

            // Check if other exist with this email
            if (existing != null && existing.getID() != getID()) {
                error("email.exists");
                throw new AbortProcessingException("Email already exists");
            }

            // Check if there is a combination of first and last name
            if (existing != null && existing.getID() != getID() &&
                    existing.getFirstName().equals(firstName)
                    && existing.getLastName().equals(lastName)) {
                error("combination.first.last.exist");
                throw new AbortProcessingException("First and Last Name already exits");
            }

            userService.update(getID(), email, password, firstName, lastName, phone, role);
        }
    }

    public void deleteUser(ActionEvent event) {
        if (selected == null) {
            LOG.log(Level.SEVERE, "deleteUser() there isn't any User selected");
            throw new AbortProcessingException("deleteUser() there isn't any User selected");
        }

        userService.delete(selected.getID());
    }
}
