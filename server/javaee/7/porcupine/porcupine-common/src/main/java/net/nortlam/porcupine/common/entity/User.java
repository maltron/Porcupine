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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Entity(name="User")
@Table(name="PORCUPINE_USER", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"FIRST_NAME", "LAST_NAME"})
})
@NamedQueries({
    @NamedQuery(name=User.FIND_BY_EMAIL,query="SELECT user FROM User user WHERE user.email=:EMAIL"),
    @NamedQuery(name=User.FIND_BY_FIRST_LAST_NAME, 
            query="SELECT user FROM User user WHERE user.firstName=:FIRSTNAME AND user.lastName=:LASTNAME")
})
@XmlRootElement(name="User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable {
    
    public static final String FIND_BY_EMAIL = "User.findByEmail()";
    public static final String FIND_BY_FIRST_LAST_NAME = "User.findByFirstLastName()";
    
    private static final Logger LOG = Logger.getLogger(User.class.getName());
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USER_ID", unique = false)
    @XmlAttribute(name="ID", required=true)
    private long ID;
    
    public static final int LENGTH_EMAIL = 70;
    @Column(name="EMAIL", nullable = false, length = LENGTH_EMAIL, unique = true)
    @XmlElement(name="Email", type=String.class, required=true)
    private String email;
    
    public static final int LENGTH_PASSWORD = 255;
    @Column(name="PASSWORD", nullable = false, length = LENGTH_PASSWORD)
    @XmlElement(name="Password", type=String.class, required=true)
    private String password;
    
    public static final int LENGTH_FIRST_NAME = 70;
    @Column(name="FIRST_NAME", nullable = false, length = LENGTH_FIRST_NAME)
    @XmlElement(name="FirstName", type=String.class, required=true)
    private String firstName;

    public static final int LENGTH_LAST_NAME = 70;
    @Column(name="LAST_NAME", nullable = false, length = LENGTH_LAST_NAME)
    @XmlElement(name="LastName", type=String.class, required=true)
    private String lastName;
    
    @Embedded
    @XmlElement(name="Phone", type=Phone.class, required=false)
    private Phone phone;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ROLE_ID", nullable = false)
    private Role role;

    public User() {
    }

    public User(String email, String password, String firstName, String lastName, 
                                                        Phone phone, Role role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (int) (this.ID ^ (this.ID >>> 32));
        hash = 23 * hash + Objects.hashCode(this.email);
        hash = 23 * hash + Objects.hashCode(this.password);
        hash = 23 * hash + Objects.hashCode(this.firstName);
        hash = 23 * hash + Objects.hashCode(this.lastName);
        hash = 23 * hash + Objects.hashCode(this.phone);
        hash = 23 * hash + Objects.hashCode(this.role);
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
        final User other = (User) obj;
        if (this.ID != other.ID) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.phone, other.phone)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
//        Writer writer = new CharArrayWriter();
//        try {
//            Marshaller m = JAXBContext.newInstance(User.class, 
//                    Role.class, Phone.class).createMarshaller();
//            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
//            
//            m.marshal(this, writer);
//        } catch(JAXBException ex) {
//            LOG.log(Level.SEVERE, "toString() UNABLE TO PARSE XML CONTENT:{0}",
//                    ex.getMessage());
//        } finally {
//            try{writer.close();}catch(IOException ex){}
//        }
//        
//        return writer.toString();
        StringBuilder builder = new StringBuilder();
        builder.append("<User ID=\"").append(ID).append("\">");
        builder.append("<Email>").append(email != null ? email : "NULL")
                .append("</Email>");
        builder.append("<FirstName>").append(firstName != null ? firstName : "NULL")
                .append("</FirstName>");
        builder.append("<LastName>").append(lastName != null ? lastName : "NULL")
                .append("</LastName>");
        builder.append("<Password>").append(password != null ? password : "NULL")
                .append("</Password>");
        builder.append(phone != null ? phone.toString() : null);
        builder.append(role != null ? role.toString() : null);
        
        return builder.toString();
    }
}
