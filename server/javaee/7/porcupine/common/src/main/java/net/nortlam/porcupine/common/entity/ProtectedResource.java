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
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Entity(name="ProtectedResource")
@Table(name="PORCUPINE_PROTECTED_RESOURCE")
@NamedQueries(
    @NamedQuery(name=ProtectedResource.FIND_BY_SCOPE, 
            query="SELECT protected_resource FROM Scope scope JOIN scope.protectedResources protected_resource WHERE scope.ID=:SCOPE_ID")
)
@XmlRootElement(name = "ProtectedResource")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProtectedResource implements Serializable {
    
    private static final Logger LOG = Logger.getLogger(ProtectedResource.class.getName());

    public static final String FIND_BY_CLIENT = "ProtectedResource.findByClient()";
    public static final String FIND_BY_SCOPE = "ProtectedResource.findByScope()";
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PROTECTED_RESOURCE_ID", unique = false, nullable = false)
    @XmlAttribute(name="ID", required = true)
    private long ID;

    public static final int LENGTH_NAME = 255;
    @Column(name="RESOURCE", length = LENGTH_NAME, nullable = false)
    @XmlElement(name="Resource", type=String.class, required=true)
    private String resource;
    
    public ProtectedResource() {
    }

    public ProtectedResource(String resource) {
        this.resource = resource;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (this.ID ^ (this.ID >>> 32));
        hash = 71 * hash + Objects.hashCode(this.resource);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
//        LOG.log(Level.INFO, "equals() [#1] this:{0} obj:{1}", new Object[] {
//            this, obj});
        if (obj == null) {
//            LOG.log(Level.INFO, "equals() [#2] obj == null  Returning false");
            return false;
        }
        if (getClass() != obj.getClass()) {
//            LOG.log(Level.INFO, "equals() [#3] getClass():{0} obj.getClass():{1} Returning false",
//                    new Object[] {getClass(), obj.getClass()});
            return false;
        }
        final ProtectedResource other = (ProtectedResource) obj;
        if (this.ID != other.ID) {
//            LOG.log(Level.INFO, "equals() [#4] Returning false");
            return false;
        }
        if (!Objects.equals(this.resource, other.resource)) {
//            LOG.log(Level.INFO, "equals() [#5] Returning false");
            return false;
        }
        
//        LOG.log(Level.INFO, "equals() [#6] Returning true");
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<ProtectedResource ID=\"").append(this.ID).append("\">");
        builder.append("<Resource>").append(resource != null ? resource : "NULL").append("</Resource>");
        builder.append("</ProtectedResource>");
        
        return builder.toString();
    }
    
    public static ProtectedResource parse(String content) throws JAXBException {
        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
        ProtectedResource pr = null;
        try {
            pr = (ProtectedResource)JAXBContext
                    .newInstance(ProtectedResource.class)
                        .createUnmarshaller().unmarshal(
                                new ByteArrayInputStream(content.getBytes()));
        } finally {
            try{stream.close();}catch(IOException ex){}
        }
        
        return pr;    
    }
    
}