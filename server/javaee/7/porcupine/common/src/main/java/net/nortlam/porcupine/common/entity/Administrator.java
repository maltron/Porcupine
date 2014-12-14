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
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Entity(name="Administrator")
@Table(name="PORCUPINE_ADMINISTRATOR", 
        indexes = {@Index(name="INDEX_ADMIN_EMAIL", columnList="EMAIL")})
//        uniqueConstraints = @UniqueConstraint(columnNames={"NAME", "EMAIL"}))

@NamedQueries({
   @NamedQuery(name=Administrator.FIND_BY_NAME_EMAIL, query="SELECT a FROM Administrator a WHERE a.name=:NAME AND a.email=:EMAIL"),
   @NamedQuery(name=Administrator.FIND_BY_CLIENT_JOINED, query="SELECT a FROM Client client JOIN client.administrators a WHERE a.ID=:ADMIN_ID")
})
@XmlRootElement(name="Administrator")
@XmlAccessorType(XmlAccessType.FIELD)
public class Administrator implements Serializable {

    public static final String FIND_BY_CLIENT = "Administrator.findByClient()";
    public static final String FIND_BY_NAME_EMAIL = "Administrator.findByNameEmail()";
    public static final String FIND_BY_CLIENT_JOINED = "Administrator.findByClientJoin()";
    
    private static final Logger LOG = Logger.getLogger(Administrator.class.getName());

    public static final int LENGTH_NAME = 70;
    public static final int LENGTH_EMAIL = 70;
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ADMINISTRATOR_ID", unique = false)
    private long ID;
    
    @Column(name="EMAIL", nullable = false, length = LENGTH_EMAIL, unique = false)
    @XmlElement(name="EMail", type=String.class, required=true)
    private String email;
    
    @Column(name="NAME", nullable = false, length = LENGTH_NAME, unique = false)
    @XmlElement(name="Name", type=String.class, required=true)
    private String name;
    
    @Embedded
    @XmlElement(name="Phone", type=Phone.class, required=false)
    private Phone phone;

    public Administrator() {
    }

    public Administrator(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Administrator(String name, String email, Phone phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public Administrator(long ID, String email, String name, Phone phone) {
        this.ID = ID;
        this.email = email;
        this.name = name;
        this.phone = phone;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (int) (this.ID ^ (this.ID >>> 32));
        hash = 11 * hash + Objects.hashCode(this.email);
        hash = 11 * hash + Objects.hashCode(this.name);
        hash = 11 * hash + Objects.hashCode(this.phone);
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
        final Administrator other = (Administrator) obj;
        if (this.ID != other.ID) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.phone, other.phone)) {
            return false;
        }
        return true;
    }
}
