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
import java.util.List;
import java.util.Set;
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
import net.nortlam.porcupine.common.entity.Administrator;
import net.nortlam.porcupine.common.entity.Client;
import net.nortlam.porcupine.common.entity.Phone;
import net.nortlam.porcupine.common.entity.ProtectedResource;
import net.nortlam.porcupine.common.entity.Scope;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.exception.InvalidScopeException;
import net.nortlam.porcupine.common.exception.UnauthorizedClientException;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Stateless
public class ClientService implements Serializable {

    private static final Logger LOG = Logger.getLogger(ClientService.class.getName());
    
    @PersistenceContext
    private EntityManager entityManager;

    public ClientService() {
    }
    
    public String create(String ID, String secret, String name,
            String description){
        Client newclient = new Client(ID, secret, name, description);
        entityManager.persist(newclient);
        
        return newclient.getID();
    }
    
    public Client read(String ID) {
        return entityManager.find(Client.class, ID);
    }
    
    public void update(String ID, String secret, String name, String description, boolean enabled) {
        Client existing = read(ID);
        existing.setEnabled(enabled);
        existing.setSecret(secret);
        existing.setName(name);
        existing.setDescription(description);
    }
    
    public void delete(String ID) {
        Client existing = read(ID);
        entityManager.remove(existing);
    }
    
    public Scope readScope(long ID) {
        return entityManager.find(Scope.class, ID);
    }
    
    public Administrator readAdministrator(long ID) {
        return entityManager.find(Administrator.class, ID);
    }
    
    public void updateAdministrator(long ID, String name, String email, Phone phone) {
        Administrator existing = readAdministrator(ID);
        existing.setName(name);
        existing.setEmail(email);
        existing.setPhone(phone);
    }
    
    public void addAdministrator(String clientID, String name, String email, Phone phone) {
        Administrator newadmin = new Administrator(name, email, phone);
        Client existing = read(clientID);
        existing.getAdministrators().add(newadmin);
    }
    
    public void deleteAdministrator(String clientID, Administrator administrator) {
        LOG.log(Level.INFO, "deleteAdministrator() Administrator ID:{0}", administrator.getID());
        // Important: The existing Client ID must exist
        Client existing = read(clientID);
        for(Administrator scan: existing.getAdministrators())
            if(scan.getID() == administrator.getID()) {
                existing.getAdministrators().remove(scan); 
                entityManager.remove(scan);
                break;
            }
    }
    
    public long addScope(String clientID, String name, String description, String message,
            boolean isAuthorizationCodeGrant, boolean isImplicitGrant, 
            boolean isResourceOwnerPasswordCredentials, boolean isClientCredentials, 
            String username, String password, int expiration, Set<ProtectedResource> resources) {
        LOG.log(Level.INFO, "addScope() ClientID:{0} Name:{1} Description:{2} Message:{3} "+
                " isAuthorizationCodeGrant:{4} isImplicit:{5} isResourceOwner:{6} isClient:{7} "+
                " Username:{8} Password:{9} Expiration:{10} Resources:{11}",
                new Object[] { clientID, name, description, message, isAuthorizationCodeGrant,
                    isImplicitGrant, isResourceOwnerPasswordCredentials, isClientCredentials,
                    username, password, expiration, resources});
        
        Client client = read(clientID);
        
        Scope newscope = new Scope(name, description, message, isAuthorizationCodeGrant, isImplicitGrant,
        isResourceOwnerPasswordCredentials, isClientCredentials, username, password, expiration);
        entityManager.persist(newscope);
        client.getScopes().add(newscope);
        
        // Add all the Protected Resources to this 
        if(resources != null && !resources.isEmpty()) {
            LOG.log(Level.INFO, "addScope() RESOURCES IS NOT EMPTY");
            
            for(ProtectedResource protectedResource: resources) {
                LOG.log(Level.INFO, "addScope() Persisting a newly Protected Resource");
                entityManager.persist(protectedResource);
                LOG.log(Level.INFO, "addScope() Adding into the new Scopes");
                newscope.addProtectedResource(protectedResource);
            }
        }
        
        return newscope.getID();
    }
    
    public void editScope(long scopeID, String name, String description, String message,
            boolean isAuthorizationCodeGrant, boolean isImplicitGrant, 
            boolean isResourceOwnerPasswordCredentials, boolean isClientCredentials, 
            String username, String password, int expiration, Set<ProtectedResource> resources) {
        Scope scope = readScope(scopeID);
        scope.setName(name);
        scope.setDescription(description);
        scope.setMessage(message);
        scope.setAuthorizationCodeGrant(isAuthorizationCodeGrant);
        scope.setImplicitGrant(isImplicitGrant);
        scope.setResourceOwnerPasswordCredentials(isResourceOwnerPasswordCredentials);
        scope.setClientCredentials(isClientCredentials);
        scope.setUsername(username);
        scope.setPassword(password);
        scope.setExpiration(expiration);
        scope.setProtectedResources(resources);
        
        entityManager.merge(scope);
    }
    
    public void deleteScope(String clientID, Scope scope) {
        deleteScope(clientID, scope.getID());
    }
    
    public void deleteScope(String clientID, long scopeID) {
        Client client = read(clientID);
        Scope scope = readScope(scopeID);
        client.getScopes().remove(scope);
        entityManager.remove(scope);
    }
    
    public boolean alreadyExistName(String name) {
        return findByName(name) != null;
    }
    
    public Client findByName(String name) {
        try {
            return entityManager.createNamedQuery(Client.FIND_BY_NAME, Client.class)
                    .setParameter("NAME", name).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "findByName() NO RESULT EXCEPTION:{0}",
                    ex.getMessage());
        }
        
        return null;
    }
    
    public List<ProtectedResource> findProtectedResourceByClient(Client client) {
        return findProtectedResourceByClient(client.getID());
    }
    
    public List<ProtectedResource> findProtectedResourceByClient(String clientID) {
        return entityManager.createNamedQuery(ProtectedResource.FIND_BY_CLIENT, ProtectedResource.class)
                .setParameter("CLIENT_ID", clientID).getResultList();
    }
    
    public List<ProtectedResource> findProtectedResourcesByScope(Scope scope) {
        return findProtectedResourcesByScope(scope.getID());
    }
    
    public List<ProtectedResource> findProtectedResourcesByScope(long scopeID) {
        return entityManager.createNamedQuery(ProtectedResource.FIND_BY_SCOPE, ProtectedResource.class)
                .setParameter("SCOPE_ID", scopeID).getResultList();
    }
    
    public List<Scope> findScopesByClient(Client client) {
        return findScopesByClient(client.getID());
    }
    
    public List<Scope> findScopesByClient(String clientID) {
        return entityManager.createNamedQuery(Scope.FIND_BY_CLIENT, Scope.class)
                .setParameter("CLIENT_ID", clientID).getResultList();
    }
    
    public List<Administrator> findAdministratorsByClient(Client client) {
        return findAdministratorsByClient(client.getID());
    }
    
    public List<Administrator> findAdministratorsByClient(String clientID) {
        return entityManager.createNamedQuery(Administrator.FIND_BY_CLIENT, Administrator.class)
            .setParameter("CLIENT_ID", clientID).getResultList();
    }
    
    public boolean alreadyExistAdministrator(String name, String email) {
        return findByNameAndEmail(name, email) != null;
    }
    
    public Administrator findByNameAndEmail(String name, String email) {
        try {
            return entityManager.createNamedQuery(Administrator.FIND_BY_NAME_EMAIL, Administrator.class)
                    .setParameter("NAME", name).setParameter("EMAIL", email).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "findByNameAndEmail() Unable to find Administrator Name:{0} and Email:{1}",
                    new Object[] {name, email});
        }
        
        return null;
    }
    
    public Client findByID(String clientID) {
        try {
            return entityManager.createNamedQuery(Client.FIND_BY_ID, Client.class)
                .setParameter("CLIENT_ID", clientID).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "findByID() NO RESULT EXCEPTION:{0}",
                    ex.getMessage());
        }
        
        return null;
    }
    
    public Scope findScopeByID(long scopeID)  {
        try {
            return entityManager.createNamedQuery(Scope.FIND_BY_ID, Scope.class)
                .setParameter("SCOPE_ID", scopeID).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "### ClientService.findScopeByID() "+
                    " NO RESULT EXCEPTION:{0}", ex.getMessage());
        }
        
        return null;
    }
    
    public Scope findScopeByName(String name) {
        try {
            return entityManager.createNamedQuery(Scope.FIND_BY_NAME, Scope.class)
                    .setParameter("NAME", name).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "### ClientService.findByName() Unable to "+
                    "find Scope by Name:{0} NO RESULT EXCEPTION:{1}",
                    new Object[] {name, ex.getMessage()});
        }
        
        return null;
    }
    
    public boolean existScopeInClient(Client client, String scopeName) {
        if(client == null) return false; 
        if(scopeName == null) return false;
        
        return doesScopeExistInClient(client, scopeName) != null;
    }
    
    public Scope doesScopeExistInClient(Client client, String scopeName) {
        if(client == null) return null;
        if(scopeName == null) return null;
        
        Scope found = null;
        for(Scope scope: client.getScopes())
            if(scope.getName().equals(scopeName)) {
                found = scope; break;
            }
        
        return found;
    }
    
    public Scope findScopeByUsernamePassword(String username, String password) {
        try {
            return entityManager.createNamedQuery(Scope.FIND_BY_USERNAME_PASSWORD, Scope.class)
                    .setParameter("USERNAME", username)
                    .setParameter("PASSWORD", password).getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "retrieveAccessToken() Unable to find "+
              " Scope based on Username:{0} and Password:{1}. NO RESULT EXCEPTION:{3}", 
                    new Object[] {username, password, ex.getMessage()});
        }
        
        return null;
    }
    
    
    public boolean existProtectedResourceInScope(Scope scope, String redirectURI) {
        if(scope == null) return false;
        
        boolean found = false;
        for(ProtectedResource resource: scope.getProtectedResources())
            if(found = resource.getResource().equals(redirectURI)) break;
                
        return found;
    }
    
    
    
    public List<Client> findAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery();
        Root<Client> root = criteria.from(Client.class);
        criteria.select(root).orderBy(builder.asc(root.get("name")));
        
        return entityManager.createQuery(criteria).getResultList();
    }
    
    public int count() {
        CriteriaQuery criteria = entityManager.getCriteriaBuilder().createQuery();
        Root<Client> root = criteria.from(Client.class);
        criteria.select(entityManager.getCriteriaBuilder().count(root));
        
        Query query = entityManager.createQuery(criteria);
        return ((Long)query.getSingleResult()).intValue();
    }
}
