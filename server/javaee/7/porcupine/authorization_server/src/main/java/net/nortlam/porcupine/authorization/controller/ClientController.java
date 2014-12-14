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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import net.nortlam.porcupine.common.entity.Administrator;
import net.nortlam.porcupine.common.entity.Client;
import net.nortlam.porcupine.common.entity.Phone;
import net.nortlam.porcupine.common.entity.PhoneType;
import net.nortlam.porcupine.common.entity.ProtectedResource;
import net.nortlam.porcupine.common.entity.Scope;
import net.nortlam.porcupine.authorization.service.ClientService;
import net.nortlam.porcupine.common.IDFactory;
import net.nortlam.porcupine.common.InitParameter;

/**
 *
 * @author Mauricio "Maltron" Leal
 */
@Named("client")
@ViewScoped
public class ClientController extends AbstractController implements Serializable {

    private static final Logger LOG = Logger.getLogger(ClientController.class.getName());

    @Context
    private ServletContext context;
    
    @EJB
    private ClientService clientService;
    
    private String operation;
    
    private String ID;
    private String secret;
    private boolean enabled;
    private String name;
    private String description;

    private Client clientSelected;

    // Administrator
    private long administratorID;
    private String administratorName;
    private String administratorEmail;
    private PhoneType administratorPhoneType;
    private String administratorAreaCode;
    private String administratorNumber;
    private String administratorBranch;

    private PhoneType administratorPhoneTypeSelected;

    private Administrator administratorSelected;

    // Scope
    private long scopeID;
    private String scopeName;
    private String scopeDescription;
    private String scopeMessage;
    private boolean scopeIsAuthorizationCode;
    private boolean scopeIsImplicit;
    private boolean scopeIsResourceOwnerPasswordCredentials;
    private boolean scopeIsClientCredentials;
    private String scopeUsername;
    private String scopePassword;
    private int scopeExpiration;
    private ProtectedResource protectedResourceSelected;
    private Set<ProtectedResource> scopeProtectedResources;

    private Scope scopeSelected;

    // Protected Resources
    private long protectedResourceID;
    private String protectedResourceResource;

    public ClientController() {
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    // INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION 
    // Client Information 
    public String getID() {
//        LOG.log(Level.INFO, "getID() ID:{0}", this.ID);
        
        return ID;
    }
    
    public void newID() {
        setOperation("CREATE");
        setID(null);
    }
    
    public void setID(String ID) {
        LOG.log(Level.INFO, "setID() ID:{0}", ID);

        if (ID == null) {
            // Load default values
            IDFactory factory = IDFactory.getInstance();
            this.enabled = true;
            this.ID = factory.newClientID();
            this.secret = factory.newClientSecret();
            this.name = null;
            this.description = null;

        } else {
            // Load values from database
            Client client = clientService.read(ID);
            this.enabled = client.isEnabled();
            this.ID = client.getID();
            this.secret = client.getSecret();
            this.name = client.getName();
            this.description = client.getDescription();
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Client getClientSelected() {
        return clientSelected;
    }

    public void setClientSelected(Client clientSelected) {
        this.clientSelected = clientSelected;
    }

    public List<Client> getClients() {
        return clientService.findAll();
    }

    public int getClientCount() {
        return clientService.count();
    }
    
    public void saveClient(ActionEvent event) {
        
        if(getOperation() != null && getOperation().equals("CREATE")) { // Create
            // Check if the name already exists
            if(clientService.alreadyExistName(getName())) {
                error("client.name.already.exist");
                throw new AbortProcessingException("Client's name already exist");
            }
            
            clientService.create(getID(), getSecret(), getName(), getDescription());
            info("client.new.created");
        } else {
            Client existing = clientService.findByName(getName());
            if(existing != null && existing.getID() != getID()) {
                // There is a name taken for this client
                error("client.name.already.exist");
                throw new AbortProcessingException("Client's name already exist");
            }
            
            clientService.update(getID(), getSecret(), getName(), getDescription(), isEnabled());
            info("client.updated");
        }
    }
    
    public void deleteClient(ActionEvent event) {
        String clientID = (String)event.getComponent().getAttributes().get("clientID");
        clientService.delete(clientID);
    }

    // ADMINISTRATOR ADMINISTRATOR ADMINISTRATOR ADMINISTRATOR ADMINISTRATOR 
    // Client's Administrator
    public long getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(long administratorID) {
        this.administratorID = administratorID;
    }

    public String getAdministratorName() {
        return administratorName;
    }

    public void setAdministratorName(String administratorName) {
        this.administratorName = administratorName;
    }

    public String getAdministratorEmail() {
        return administratorEmail;
    }

    public void setAdministratorEmail(String administratorEmail) {
        this.administratorEmail = administratorEmail;
    }

    public PhoneType getAdministratorPhoneTypeSelected() {
        return administratorPhoneTypeSelected;
    }

    public void setAdministratorPhoneTypeSelected(PhoneType administratorPhoneTypeSelected) {
        this.administratorPhoneTypeSelected = administratorPhoneTypeSelected;
    }

    public PhoneType[] getPhoneTypes() {
        return PhoneType.values();
    }

    public PhoneType getAdministratorPhoneType() {
        return administratorPhoneType;
    }

    public void setAdministratorPhoneType(PhoneType administratorPhoneType) {
        this.administratorPhoneType = administratorPhoneType;
    }

    public String getAdministratorAreaCode() {
        return administratorAreaCode;
    }

    public void setAdministratorAreaCode(String administratorAreaCode) {
        this.administratorAreaCode = administratorAreaCode;
    }

    public String getAdministratorNumber() {
        return administratorNumber;
    }

    public void setAdministratorNumber(String administratorNumber) {
        this.administratorNumber = administratorNumber;
    }

    public String getAdministratorBranch() {
        return administratorBranch;
    }

    public void setAdministratorBranch(String administratorBranch) {
        this.administratorBranch = administratorBranch;
    }

    public Administrator getAdministratorSelected() {
        return administratorSelected;
    }

    public void setAdministratorSelected(Administrator administratorSelected) {
        this.administratorSelected = administratorSelected;
    }

    public List<Administrator> getAdministrators() {
        // IMPORTANT: The Client ID must be set
        if (getID() == null) {
            LOG.log(Level.SEVERE, "getAdministrators() ClientID IS NULL");
            return null;
        }

        return clientService.findAdministratorsByClient(getID());
    }

    public void addAdministrator() {
        this.administratorID = 0;
        this.administratorName = null;
        this.administratorEmail = null;
        this.administratorPhoneType = PhoneType.MOBILE;
         this.administratorAreaCode = null;
        this.administratorNumber = null;
        this.administratorBranch = null;
    }

    public void editAdministrator() {
        this.administratorID = administratorSelected != null ? administratorSelected.getID() : 0;
        this.administratorName = administratorSelected != null ? administratorSelected.getName() : null;
        this.administratorEmail = administratorSelected != null ? administratorSelected.getEmail() : null;

        Phone phone = administratorSelected != null ? administratorSelected.getPhone() : null;
        this.administratorPhoneType = phone != null ? phone.getPhoneType() : PhoneType.MOBILE;
        this.administratorAreaCode = phone != null ? phone.getAreaCode() : null;
        this.administratorNumber = phone != null ? phone.getNumber() : null;
        this.administratorBranch = phone != null ? phone.getBranch() : null;
    }

    public void saveAdministrator() {
        Phone phone;
        if(administratorAreaCode == null && administratorNumber == null && administratorBranch == null)
            phone = null;
        else  phone = new Phone(administratorPhoneType, administratorAreaCode,
                                                            administratorNumber, administratorBranch);
        
        if (this.administratorID > 0) {// UPDATE
            clientService.updateAdministrator(administratorID, administratorName, administratorEmail, phone);
            info("administrator.updated");
        } else {
            // Before saving, check if the combination of name and email already exist
            if(clientService.alreadyExistAdministrator(administratorName, administratorEmail)) {
                LOG.log(Level.WARNING, "saveAdministrator() Combination of Name:{0} and E-Mail:{1} already exists",
                        new Object[] {administratorName, administratorEmail});
                error("administrator.combination.name.email");
                throw new AbortProcessingException();
            }
            
            clientService.addAdministrator(getID(), administratorName, administratorEmail, phone);
            info("administrator.new.created");
        }
    }

    public void deleteAdministrator() {
        // Important: It depends on a Selected object
        if (administratorSelected == null) {
            LOG.log(Level.SEVERE, "deleteAdministrator() UNABLE TO DELETE. There isn't anything selected");
            return;
        }

        clientService.deleteAdministrator(getID(), administratorSelected);
    }

    // SCOPES SCOPES SCOPES SCOPES SCOPES SCOPES SCOPES SCOPES SCOPES SCOPES SCOPES 
    // Scopes
    public long getScopeID() {
        return scopeID;
    }

    public void setScopeID(long scopeID) {
        this.scopeID = scopeID;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public String getScopeDescription() {
        return scopeDescription;
    }

    public void setScopeDescription(String scopeDescription) {
        this.scopeDescription = scopeDescription;
    }

    public String getScopeMessage() {
        return scopeMessage;
    }

    public void setScopeMessage(String scopeMessage) {
        this.scopeMessage = scopeMessage;
    }

    public boolean isScopeIsAuthorizationCode() {
        return scopeIsAuthorizationCode;
    }

    public void setScopeIsAuthorizationCode(boolean scopeIsAuthorizationCode) {
        this.scopeIsAuthorizationCode = scopeIsAuthorizationCode;
    }

    public boolean isScopeIsImplicit() {
        return scopeIsImplicit;
    }

    public void setScopeIsImplicit(boolean scopeIsImplicit) {
        this.scopeIsImplicit = scopeIsImplicit;
    }

    public boolean isScopeIsResourceOwnerPasswordCredentials() {
        return scopeIsResourceOwnerPasswordCredentials;
    }

    public void setScopeIsResourceOwnerPasswordCredentials(boolean scopeIsResourceOwnerPasswordCredentials) {
        this.scopeIsResourceOwnerPasswordCredentials = scopeIsResourceOwnerPasswordCredentials;
    }

    public boolean isScopeIsClientCredentials() {
        return scopeIsClientCredentials;
    }

    public void setScopeIsClientCredentials(boolean scopeIsClientCredentials) {
        this.scopeIsClientCredentials = scopeIsClientCredentials;
    }

    public String getScopeUsername() {
        return scopeUsername;
    }

    public void setScopeUsername(String scopeUsername) {
        this.scopeUsername = scopeUsername;
    }

    public String getScopePassword() {
        return scopePassword;
    }

    public void setScopePassword(String scopePassword) {
        this.scopePassword = scopePassword;
    }

    public int getScopeExpiration() {
        return scopeExpiration;
    }

    public void setScopeExpiration(int scopeExpiration) {
        this.scopeExpiration = scopeExpiration;
    }

    public Scope getScopeSelected() {
        return scopeSelected;
    }

    public void setScopeSelected(Scope scopeSelected) {
        this.scopeSelected = scopeSelected;
    }

    public List<Scope> getScopes() {
        // IMPORTANT: Client ID must be set
        if (getID() == null) {
            LOG.log(Level.SEVERE, "getScopes() ClientIS IS NULL");
            return null;
        }

        return clientService.findScopesByClient(getID());
    }

    public void addScope(ActionEvent event) {
        LOG.log(Level.INFO, "addScope()");
        
        this.scopeID = 0;
        this.scopeName = null;
        this.scopeDescription = null;
        this.scopeMessage = null;
        this.scopeIsAuthorizationCode = true;
        this.scopeIsImplicit = false;
        this.scopeIsResourceOwnerPasswordCredentials = false;
        this.scopeIsClientCredentials = false;
        this.scopeUsername = null;
        this.scopePassword = null;
        this.scopeExpiration = InitParameter.parameterAccessTokenExpiration(context);
        this.scopeProtectedResources = new HashSet<ProtectedResource>();
        this.protectedResourceSelected = null;
        this.protectedResourceResource = null;
    }
    
    public void editScope(ActionEvent event) {
        LOG.log(Level.INFO, "editScope()");
        
        this.scopeID = scopeSelected != null ? scopeSelected.getID() : 0;
        this.scopeName = scopeSelected != null ? scopeSelected.getName() : null;
        this.scopeDescription = scopeSelected != null ? scopeSelected.getDescription() : null;
        this.scopeMessage = scopeSelected != null ? scopeSelected.getMessage() : null;
        this.scopeIsAuthorizationCode = scopeSelected != null ? scopeSelected.isAuthorizationCodeGrant() : true;
        this.scopeIsImplicit = scopeSelected != null ? scopeSelected.isImplicitGrant() : false;
        this.scopeIsResourceOwnerPasswordCredentials = scopeSelected != null ? 
                scopeSelected.isResourceOwnerPasswordCredentials() : false;
        this.scopeIsClientCredentials = scopeSelected != null ? scopeSelected.isClientCredentials() : false;
        this.scopeUsername = scopeSelected != null ? scopeSelected.getUsername() : null;
        this.scopePassword = scopeSelected != null ? scopeSelected.getPassword() : null;
        this.scopeExpiration = scopeSelected != null ? scopeSelected.getExpiration() : 0;
        this.protectedResourceResource = null;
        
        this.scopeProtectedResources = new HashSet<ProtectedResource>();
        if(scopeSelected != null)
            for(ProtectedResource pr: scopeSelected.getProtectedResources())
                scopeProtectedResources.add(pr);
    }
    
    public void saveScope(ActionEvent event) {
        if(this.scopeID == 0) { // CREATE 
            this.scopeID = clientService.addScope(getID(), this.scopeName, 
                    this.scopeDescription, this.scopeMessage, 
                    this.isScopeIsAuthorizationCode(), this.isScopeIsImplicit(),
                    this.isScopeIsResourceOwnerPasswordCredentials(), 
                    this.isScopeIsClientCredentials(),
                    this.scopeUsername, this.scopePassword, this.scopeExpiration,
                    this.scopeProtectedResources);
        } else { // UPDATE
            clientService.editScope(this.scopeID, this.scopeName,
                    this.scopeDescription, this.scopeMessage, 
                    this.isScopeIsAuthorizationCode(), this.isScopeIsImplicit(),
                    this.isScopeIsResourceOwnerPasswordCredentials(), 
                    this.isScopeIsClientCredentials(),
                    this.scopeUsername, this.scopePassword, this.scopeExpiration,
                    this.scopeProtectedResources);
        }
    }
    
    public void deleteScope(ActionEvent event) {
        // Important: A Scope must be selected
        if(this.scopeSelected == null) {
            LOG.log(Level.WARNING, "deleteScope() There isn't any Scope selected");
            return;
        }
        
        clientService.deleteScope(getID(), this.scopeSelected);
    }
    
    public ProtectedResource getProtectedResourceSelected() {
        return protectedResourceSelected;
    }

    public void setProtectedResourceSelected(ProtectedResource protectedResourceSelected) {
        this.protectedResourceSelected = protectedResourceSelected;
    }
    
    public void addProtectedResource(ActionEvent event) {
        LOG.log(Level.INFO, "addProtectedResource()");
        if(scopeProtectedResources == null) 
            scopeProtectedResources = new HashSet<ProtectedResource>();
        
        scopeProtectedResources.add(new ProtectedResource(this.protectedResourceResource));
        this.protectedResourceResource = null;
    }
    
    public void deleteProtectedResource(ActionEvent event) {
        LOG.log(Level.INFO, "deleteProtectedResource()");
        if(this.protectedResourceSelected == null) {
            LOG.log(Level.WARNING, "deleteProtectedResource() Protected Resource Selected is NULL");
            return;
        }
        
        scopeProtectedResources.remove(this.protectedResourceSelected);
    }
    
    // PROTECTED RESOURCE PROTECTED RESOURCE PROTECTED RESOURCE PROTECTED RESOURCE 
    // Protected Resource
    public long getProtectedResourceID() {
        return protectedResourceID;
    }

    public void setProtectedResourceID(long protectedResourceID) {
        this.protectedResourceID = protectedResourceID;
    }

    public String getProtectedResourceResource() {
        return protectedResourceResource;
    }

    public void setProtectedResourceResource(String protectedResourceResource) {
        this.protectedResourceResource = protectedResourceResource;
    }

    public Set<ProtectedResource> getProtectedResources() {
        return scopeProtectedResources;
    }
    
    // TEST TEST TEST TEST TEST TEST 
    private List<String> v = new ArrayList<String>();
    private String value;
    private String valueToAdd;

    @PostConstruct 
    private void init() {
        v.add("One");
        v.add("Two");
        v.add("Three");
    }
    
    public List<String> getValues() {
        return v;
    }
    
    public void setValueToAdd(String valueToAdd) {
        this.valueToAdd = valueToAdd;
    }
    
    public String getValueToAdd() {
        return valueToAdd;
    }
    
    public void setValueSelected(String value) {
        this.value = value;
    }
    
    public String getValueSelected() {
        return value;
    }
    
    public void testAdd(ActionEvent event) {
        v.add(valueToAdd);
        this.valueToAdd = null;
    }
    
    public void testDelete(ActionEvent event) {
        if(value == null) {
            LOG.log(Level.WARNING, "testDelete() Value SELECTED is NULL");
            return;
        }
        
        v.remove(value);
    }
}
