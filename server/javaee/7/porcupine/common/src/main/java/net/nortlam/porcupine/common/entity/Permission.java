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
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Entity(name="Permission")
@Table(name="PORCUPINE_PERMISSION")
@XmlRootElement(name="Permission")
@XmlAccessorType(XmlAccessType.FIELD)
public class Permission implements Serializable {

    private static final Logger LOG = Logger.getLogger(Permission.class.getName());
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PERMISSION_ID", nullable = false, unique = false)
    @XmlAttribute(name="ID", required=true)
    private long ID;

    public static final int LENGTH_LOGIC = 7;
    @Enumerated(EnumType.STRING)
    @Column(name="LOGIC", nullable = false, length = LENGTH_LOGIC)
    @XmlElement(name="Logic", type=String.class, required=true)
    private PermissionLogic logic;
    
    public Permission() {
    }

    public Permission(PermissionLogic logic) {
        this.logic = logic;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public PermissionLogic getLogic() {
        return logic;
    }

    public void setLogic(PermissionLogic logic) {
        this.logic = logic;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (int) (this.ID ^ (this.ID >>> 32));
        hash = 61 * hash + Objects.hashCode(this.logic);
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
        final Permission other = (Permission) obj;
        if (this.ID != other.ID) {
            return false;
        }
        if (this.logic != other.logic) {
            return false;
        }
        return true;
    }
}
