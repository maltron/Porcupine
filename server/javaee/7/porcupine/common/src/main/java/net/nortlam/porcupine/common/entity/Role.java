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

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import static javax.xml.bind.JAXB.marshal;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.nortlam.porcupine.common.xml.XMLTransform;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Entity(name="Role")
@Table(name="PORCUPINE_ROLE")
@NamedQueries({
    @NamedQuery(name=Role.FIND_BY_ROLENAME,query="SELECT role FROM Role role WHERE role.rolename=:ROLENAME"),
    @NamedQuery(name=Role.FIND_USER,query="SELECT role FROM Role role WHERE role.rolename=\'User\'"),
    @NamedQuery(name=Role.FIND_ADMIN,query="SELECT role FROM Role role WHERE role.rolename=\'Admin\'")
})
@XmlRootElement(name="Role")
@XmlAccessorType(XmlAccessType.FIELD)
public class Role implements Serializable, XMLTransform<Role> {
    
    public static final String FIND_BY_ROLENAME = "Role.findByRolename()";
    public static final String FIND_USER = "Role.findRoleUser()";
    public static final String FIND_ADMIN = "Role.findRoleAdmin()";
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ROLE_ID", unique = false)
    @XmlAttribute(name="ID", required=true)
    private long ID;
    
    public static final int LENGTH_ROLENAME = 30;
    @Column(name="ROLENAME", length = LENGTH_ROLENAME, unique = true, nullable = false)
    @XmlElement(name="Rolename", type=String.class, required=true)
    private String rolename;
    
    public Role() {
    }

    public Role(String rolename) {
        this.rolename = rolename;
    }
    
    public Role(Role role) {
        setID(role.getID());
        setRolename(role.getRolename());
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
    
    public boolean isGod() {
        if(getRolename() == null) return false;
        
        return getRolename().equalsIgnoreCase("god");
    }
    
    public boolean isAdmin() {
        if(getRolename() == null) return false;
        
        return getRolename().equalsIgnoreCase("admin");
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (int) (this.ID ^ (this.ID >>> 32));
        hash = 23 * hash + Objects.hashCode(this.rolename);
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
        final Role other = (Role) obj;
        if (this.ID != other.ID) {
            return false;
        }
        if (!Objects.equals(this.rolename, other.rolename)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<Role ID=\">").append(ID).append("\">");
        builder.append("<Rolename>").append(rolename != null ? rolename : "NULL")
                .append("</Rolename>");
        builder.append("</Role>");
        
        return builder.toString();
    }
    
    // XML TRANSFORM XML TRANSFORM XML TRANSFORM XML TRANSFORM XML TRANSFORM XML TRANSFORM 
    //   XML TRANSFORM XML TRANSFORM XML TRANSFORM XML TRANSFORM XML TRANSFORM XML TRANSFORM 
    @Override
    public Role fromXML(String content) throws JAXBException {
        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
        try {
            Role role = (Role)JAXBContext.newInstance(Role.class)
                        .createUnmarshaller().unmarshal(
                                new ByteArrayInputStream(content.getBytes()));
            setID(role.getID());
            setRolename(role.getRolename());
            
        } finally {
            try{stream.close();}catch(IOException ex){}
        }
        
        return this;
    }

    @Override
    public String toXML() throws JAXBException {
        Writer writer = new CharArrayWriter();
        try {
            Marshaller marshaller = JAXBContext.newInstance(Role.class)
                                                    .createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    
            marshaller.marshal(this, writer);
        } finally {
            try{writer.close();}catch(IOException ex){}
        }
        
        return writer.toString();
    }
    
}
