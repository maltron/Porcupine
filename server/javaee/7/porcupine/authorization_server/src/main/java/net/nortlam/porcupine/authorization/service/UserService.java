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
package net.nortlam.porcupine.authorization.service;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.nortlam.porcupine.common.entity.Phone;
import net.nortlam.porcupine.common.entity.Role;
import net.nortlam.porcupine.common.entity.User;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Stateless
public class UserService implements Serializable {

    private static final Logger LOG = Logger.getLogger(UserService.class.getName());
    
    @PersistenceContext
    private EntityManager entityManager;

    public UserService() {
    }
    
    public long create(String email, String password, String firstName, String lastName, 
            Phone phone, Role role) {
        User newUser = new User(email, password, firstName, lastName, phone, role);
        entityManager.persist(newUser);
        
        return newUser.getID();
    }
    
    public User read(long ID) {
        return entityManager.find(User.class, ID);
    }
    
    public void update(long ID, String email, String password, String firstName, 
            String lastName, Phone phone, Role role) {
        User existingUser = new User(email, password, firstName, lastName, phone, role);
        existingUser.setID(ID);
        
        entityManager.merge(existingUser);
        
//        User existing = read(ID);
//        existing.setEmail(email);
//        existing.setFirstName(firstName);
//        existing.setLastName(lastName);
//        existing.setPhone(phone);
//        existing.setRole(role);
    }
    
    public void updatePassword(long ID, String newPassword) {
        User existing = read(ID);
        existing.setPassword(newPassword);
    }
    
    public void delete(long ID) {
        entityManager.remove(read(ID));
    }

    
    public boolean alreadyExistEmail(String email) {
        return findByEmail(email) != null;
    }
    
    public User findByPrincipal(Principal principal) {
        return findByEmail(principal.getName());
    }
    
    public User findByEmail(String email) {
        try {
            return entityManager.createNamedQuery(User.FIND_BY_EMAIL, User.class)
                .setParameter("EMAIL", email).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "findByEmail() NO RESULT EXCEPTION for Email:{0}",
                    email);
        }
        
        return null;
    }
    
    public boolean alreadyExistName(String firstName, String lastName) {
            return findByFirstLastName(firstName, lastName) != null;
    }
    
    public User findByFirstLastName(String firstName, String lastName) {
        try {
            return entityManager.createNamedQuery(User.FIND_BY_FIRST_LAST_NAME, User.class)
                .setParameter("FIRSTNAME", firstName)
                .setParameter("LASTNAME", lastName).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "findByFirstLastName() NO RESULT EXCEPTION for First Name:{1} Last Name {2}",
                    new Object[] {firstName, lastName});
        }
        
        return null;
    }
    
    public boolean alreadyExistRolename(String rolename) {
        boolean found = false;
        try {
            findByRolename(rolename);
            found = true;
        } catch(NoResultException ex) {
        }
        
        return found;
    }
    
    public Role findByRolename(String rolename) throws NoResultException {
        try {
            return entityManager.createNamedQuery(Role.FIND_BY_ROLENAME, Role.class)
                .setParameter("ROLENAME", rolename).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "findByRolename() Unable to find Rolename:{0}", rolename);
        }
        
        return null;
    }
    
    public Role getRoleUser() {
        return entityManager.createNamedQuery(Role.FIND_USER, Role.class).getSingleResult();
    }
    
    public Role getRoleAdmin() {
        return entityManager.createNamedQuery(Role.FIND_ADMIN, Role.class).getSingleResult();
    }
    
    public List<User> findAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery();
        Root<User> root = criteria.from(User.class);
        criteria.select(root).orderBy(builder.asc(root.get("firstName")));
        
        return entityManager.createQuery(criteria).getResultList();
    }
    
    public int count() {
        CriteriaQuery criteria = entityManager.getCriteriaBuilder().createQuery();
        Root<User> root = criteria.from(User.class);
        criteria.select(entityManager.getCriteriaBuilder().count(root));
        
        Query query = entityManager.createQuery(criteria);
        return ((Long)query.getSingleResult()).intValue();
    }
    
}
