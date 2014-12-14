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
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="Client")
@Table(name = "PORCUPINE_CLIENT")
@NamedQueries({
    @NamedQuery(name=Client.FIND_ALL, query="SELECT client FROM Client client ORDER BY client.name"),
    @NamedQuery(name=Client.FIND_BY_ID, query="SELECT client FROM Client client WHERE client.ID=:CLIENT_ID"),
    @NamedQuery(name=Client.FIND_BY_NAME,query="SELECT client FROM Client client WHERE client.name=:NAME"),
    @NamedQuery(name=Administrator.FIND_BY_CLIENT,query="SELECT admin FROM Client client JOIN client.administrators admin WHERE client.ID=:CLIENT_ID"),
    @NamedQuery(name=Scope.FIND_BY_CLIENT,query="SELECT scope FROM Client client JOIN client.scopes scope WHERE client.ID=:CLIENT_ID"),
    @NamedQuery(name=ProtectedResource.FIND_BY_CLIENT, query="SELECT protected_resource FROM Client client JOIN client.scopes scope JOIN scope.protectedResources protected_resource WHERE client.ID=:CLIENT_ID")
        
})
@XmlRootElement(name="Client")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client implements Serializable {

    public static final String FIND_ALL = "Client.findAll()";
    public static final String FIND_BY_ID = "Client.findByID()";
    public static final String FIND_BY_NAME = "Client.findByName()";
    
    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    
    public static final int LENGTH_CLIENT_ID = 73;
    @Id 
    @Column(name="CLIENT_ID", length = LENGTH_CLIENT_ID, nullable = false, unique = false, insertable = false, updatable = false)
    @XmlAttribute(name="ID", required = true)
    private String ID;
    
    public static final int LENGTH_SECRET = 81;
    @Column(name="SECRET", nullable = false, length = LENGTH_SECRET, unique = false, insertable = true, updatable = true)
    @XmlElement(name="Secret", type=String.class, required=true)
    private String secret;
    
    @Column(name="ENABLED", nullable = false, insertable = true, updatable = true)
    @XmlAttribute(name="Enabled", required=true)
    private boolean enabled;
    
    public static final int LENGTH_NAME = 70;
    @Column(name="NAME", length = LENGTH_NAME, nullable = false, unique = true, insertable = true, updatable = true)
    @XmlElement(name="Name", type=String.class, required=true)
    private String name;
    
    public static final int LENGTH_DESCRIPTION = 255;
    @Column(name="DESCRIPTION", length = LENGTH_DESCRIPTION, unique = false, insertable = true, updatable = true)
    @XmlElement(name="Description", type=String.class, required=false)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="PORCUPINE_CLIENT_HAS_ADMINS", 
            joinColumns = @JoinColumn(name="CLIENT_ID", referencedColumnName = "CLIENT_ID"),
            inverseJoinColumns = @JoinColumn(name="ADMINISTRATOR_ID", referencedColumnName = "ADMINISTRATOR_ID"))
    @XmlElements({@XmlElement(name="Administrator", type=String.class, required=false)})
    private Set<Administrator> administrators;
    
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="PORCUPINE_CLIENT_HAS_SCOPES",
            joinColumns=@JoinColumn(name="CLIENT_ID", referencedColumnName="CLIENT_ID"),
            inverseJoinColumns=@JoinColumn(name="SCOPE_ID", referencedColumnName="SCOPE_ID"))
    @XmlElements({@XmlElement(name="Scope", type=Scope.class, required=false)})
    private Set<Scope> scopes;

    public Client() {
    }

    public Client(String ID, String secret, String name, String description) {
        this.ID = ID;
        this.secret = secret;
        this.name = name;
        this.description = description;
        this.enabled = true;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public Set<Administrator> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(Set<Administrator> administrators) {
        this.administrators = administrators;
    }

    public Set<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(Set<Scope> scope) {
        this.scopes = scopes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.ID);
        hash = 67 * hash + Objects.hashCode(this.secret);
        hash = 67 * hash + (this.enabled ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.description);
        hash = 67 * hash + Objects.hashCode(this.administrators);
        hash = 67 * hash + Objects.hashCode(this.scopes);
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
        final Client other = (Client) obj;
        if (!Objects.equals(this.ID, other.ID)) {
            return false;
        }
        if (!Objects.equals(this.secret, other.secret)) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.administrators, other.administrators)) {
            return false;
        }
        if (!Objects.equals(this.scopes, other.scopes)) {
            return false;
        }
        return true;
    }

}
