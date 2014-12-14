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
package net.nortlam.porcupine.common.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.nortlam.porcupine.common.InitParameter;
/**
 *
 * @author Mauricio "Maltron" Leal */
@Entity(name="Scope")
@Table(name="PORCUPINE_SCOPE", 
        uniqueConstraints = {@UniqueConstraint(columnNames = {"USERNAME", "PASSWORD"})})
@NamedQueries({
    @NamedQuery(name=Scope.FIND_BY_ID, query="SELECT scope FROM Scope scope WHERE scope.ID=:SCOPE_ID"),
    @NamedQuery(name=Scope.FIND_BY_USERNAME_PASSWORD,
            query="SELECT scope FROM Scope scope WHERE scope.username=:USERNAME and scope.password=:PASSWORD"),
    @NamedQuery(name=Scope.FIND_BY_NAME, query="SELECT scope FROM Scope scope WHERE scope.name=:NAME")
})
@XmlRootElement(name="Scope")
@XmlAccessorType(XmlAccessType.FIELD)
public class Scope implements Serializable {
    
    public static final String FIND_BY_ID = "Scope.findByID()";
    public static final String FIND_BY_CLIENT = "Scope.findByClient()";
    public static final String FIND_BY_USERNAME_PASSWORD = "Scope.findByUsernamePassword()";
    public static final String FIND_BY_NAME = "Scope.findByName()";
    
    public static final int DEFAULT_EXPIRATION = 300000; // 5 minutes

    private static final Logger LOG = Logger.getLogger(Scope.class.getName());

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SCOPE_ID", nullable = false, unique = false)
    @XmlAttribute(name="ID", required=true)
    private long ID;
    
    public static final int LENGTH_NAME = 30;
    @Column(name="NAME", nullable = false, length = LENGTH_NAME, unique = true)
    @XmlElement(name="Name", type=String.class, required=true)
    private String name;
    
    public static final int LENGTH_DESCRIPTION = 255;
    @Column(name="DESCRIPTION", length = LENGTH_DESCRIPTION, nullable = true)
    @XmlElement(name="Description", type=String.class, required=false)
    private String description;
    
    public static final int LENGTH_MESSAGE = 255;
    @Column(name="MESSAGE", length = LENGTH_MESSAGE, nullable = false)
    @XmlElement(name="Message", type=String.class, required=true)
    private String message;
    
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="PORCUPINE_SCOPE_ACCESS_PROTECTED_RESOURCES",
            joinColumns=@JoinColumn(name="SCOPE_ID", referencedColumnName="SCOPE_ID"),
            inverseJoinColumns=@JoinColumn(name="PROTECTED_RESOURCE_ID", referencedColumnName="PROTECTED_RESOURCE_ID"),
            foreignKey = @ForeignKey(name = "SCOPE_ACCESS_PROTECTED_RESOURCES"))
    @XmlElements({@XmlElement(name="ProtectedResource", type=String.class, required=false)})
    private Set<ProtectedResource> protectedResources;

    @Column(name="IS_AUTHORIZATION_CODE_GRANT", nullable = true)
    @XmlElement(name="IsAuthorizationCodeGrant", type=Boolean.class, required=false)
    private boolean isAuthorizationCodeGrant;
    
    @Column(name="IS_IMPLICIT_GRANT", nullable = true)
    @XmlElement(name="IsImplicitGrant", type=Boolean.class, required=false)
    private boolean isImplicitGrant;
    
    @Column(name="IS_RESOURCE_OWNER_PASSWORD_CREDENTIALS_GRANT", nullable = true)
    @XmlElement(name="IsResourceOwnerPasswordCredentialsGrant", type=Boolean.class, required=false)
    private boolean isResourceOwnerPasswordCredentials;
    
    @Column(name="IS_CLIENT_CREDENTIALS_GRANT", nullable = true)
    @XmlElement(name="IsClientCredentialsGrant", type=Boolean.class, required=false)
    private boolean isClientCredentials;
    
    public static final int LENGTH_USERNAME = 50;
    @Column(name="USERNAME", nullable = true, length = LENGTH_USERNAME)
    @XmlElement(name="Username", type=String.class, required=false)
    private String username;    // for using Grant: Resource Owner Password Credentials
    
    public static final int LENGTH_PASSWORD = 255;
    @Column(name="PASSWORD", nullable = true, length = LENGTH_PASSWORD)
    @XmlElement(name="Password", type=String.class, required=false)
    private String password;    // for using Grant: Resource Owner Password Credentials
    
//    @ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="PERMISSION_ID", nullable = false)
//    @XmlElement(name="Permission", type=Permission.class, required=true)
//    private Permission permission;
//    
//    @Column(name="ROLENAME", nullable = true, length = Role.LENGTH_ROLENAME)
//    @XmlElement(name="Rolename", type=String.class, required=false)
//    private String rolename; // In case logic = ROLE
    
    @Column(name="EXPIRATION", nullable = false)
    @XmlElement(name="Expiration", type=int.class, required=true)
    private int expiration; // Default value used for each Access Token expiration 
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinTable(name="PORCUPINE_CLIENT_HAS_SCOPES",
            joinColumns=@JoinColumn(name="SCOPE_ID", referencedColumnName="SCOPE_ID"),
            inverseJoinColumns=@JoinColumn(name="CLIENT_ID", referencedColumnName="CLIENT_ID"))
    @XmlTransient
    private Client client;

    public Scope() {
    }

    public Scope(String name, String description, String message) {
        this.name = name;
        this.description = description;
        this.message = message;
    }

    public Scope(String name, String description, String message, 
            boolean isAuthorizationCodeGrant, boolean isImplicitGrant, 
                boolean isResourceOwnerPasswordCredentials, 
                    boolean isClientCredentials, 
                        String username, String password, int expiration) {

        this.name = name;
        this.description = description;
        this.message = message;
        this.isAuthorizationCodeGrant = isAuthorizationCodeGrant;
        this.isImplicitGrant = isImplicitGrant;
        this.isResourceOwnerPasswordCredentials = isResourceOwnerPasswordCredentials;
        this.isClientCredentials = isClientCredentials;
        this.username = username;
        this.password = password;
        this.expiration = expiration;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void addProtectedResource(ProtectedResource protectedResource) {
        if(protectedResources == null) protectedResources = new HashSet<ProtectedResource>();
        protectedResources.add(protectedResource);
    }

    public Set<ProtectedResource> getProtectedResources() {
        return protectedResources;
    }

    public void setProtectedResources(Set<ProtectedResource> protectedResources) {
        this.protectedResources = protectedResources;
    }
    
    public boolean isProtectedResources() {
        if(protectedResources == null) return false;
        
        return !protectedResources.isEmpty();
    }
  

    public boolean isAuthorizationCodeGrant() {
        return isAuthorizationCodeGrant;
    }

    public void setAuthorizationCodeGrant(boolean isAuthorizationCodeGrant) {
        this.isAuthorizationCodeGrant = isAuthorizationCodeGrant;
    }

    public boolean isImplicitGrant() {
        return isImplicitGrant;
    }

    public void setImplicitGrant(boolean isImplicitGrant) {
        this.isImplicitGrant = isImplicitGrant;
    }

    public boolean isResourceOwnerPasswordCredentials() {
        return isResourceOwnerPasswordCredentials;
    }

    public void setResourceOwnerPasswordCredentials(boolean isResourceOwnerPasswordCredentials) {
        this.isResourceOwnerPasswordCredentials = isResourceOwnerPasswordCredentials;
    }

    public boolean isClientCredentials() {
        return isClientCredentials;
    }

    public void setClientCredentials(boolean isClientCredentials) {
        this.isClientCredentials = isClientCredentials;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public Permission getPermission() {
//        return permission;
//    }
//
//    public void setPermission(Permission permission) {
//        this.permission = permission;
//    }
//
//    public String getRolename() {
//        return rolename;
//    }
//
//    public void setRolename(String rolename) {
//        this.rolename = rolename;
//    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }
    
    public Date getExpirationTimming(ServletContext context) {
        TimeZone zone = InitParameter.parameterTimeZone(context);
        Locale locale = InitParameter.parameterLocale(context);
        
        Calendar calendar = Calendar.getInstance(zone, locale); // Today's timing
        calendar.add(Calendar.MILLISECOND, getExpiration());
        
        return calendar.getTime();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (this.ID ^ (this.ID >>> 32));
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.description);
        hash = 23 * hash + Objects.hashCode(this.message);
        hash = 23 * hash + Objects.hashCode(this.protectedResources);
        hash = 23 * hash + (this.isAuthorizationCodeGrant ? 1 : 0);
        hash = 23 * hash + (this.isImplicitGrant ? 1 : 0);
        hash = 23 * hash + (this.isResourceOwnerPasswordCredentials ? 1 : 0);
        hash = 23 * hash + (this.isClientCredentials ? 1 : 0);
//        hash = 23 * hash + Objects.hashCode(this.permission);
//        hash = 23 * hash + Objects.hashCode(this.rolename);
        hash = 23 * hash + (int) (this.expiration ^ (this.expiration >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Scope other = (Scope) obj;
        if (this.ID != other.ID) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.protectedResources, other.protectedResources)) {
            return false;
        }
        if (this.isAuthorizationCodeGrant != other.isAuthorizationCodeGrant) {
            return false;
        }
        if (this.isImplicitGrant != other.isImplicitGrant) {
            return false;
        }
        if (this.isResourceOwnerPasswordCredentials != other.isResourceOwnerPasswordCredentials) {
            return false;
        }
        if (this.isClientCredentials != other.isClientCredentials) {
            return false;
        }
//        if (!Objects.equals(this.permission, other.permission)) {
//            return false;
//        }
//        if (!Objects.equals(this.rolename, other.rolename)) {
//            return false;
//        }
        if (this.expiration != other.expiration) {
            return false;
        }
        return true;
    }
}
