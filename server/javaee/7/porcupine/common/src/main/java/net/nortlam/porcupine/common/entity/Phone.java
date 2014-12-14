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
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Embeddable
@XmlRootElement(name="Phone")
@XmlAccessorType(XmlAccessType.FIELD)
public class Phone implements Serializable {

    private static final Logger LOG = Logger.getLogger(Phone.class.getName());

    public static final String SEPARATOR_TYPE = ":";
    public static final String SEPARATOR_START_AREA_CODE = "(";
    public static final String SEPARATOR_END_AREA_CODE = ")";

    public static final int LENGTH_PHONE_TYPE = 15;
    @Enumerated(EnumType.STRING)
    @Column(name="PHONE_TYPE", length = LENGTH_PHONE_TYPE, nullable = true)
    @XmlAttribute(name="Type", required = false)
    private PhoneType phoneType;
    
    public static final int LENGTH_AREA_CODE = 10;
    @Column(name="AREA_CODE", length = LENGTH_AREA_CODE, nullable = true)
    @XmlElement(name="AreaCode", type=String.class, required=false)
    private String areaCode;
    
    public static final int LENGTH_NUMBER = 12;
    @Column(name="NUMBER", length = LENGTH_NUMBER, nullable = true)
    @XmlElement(name="Number", type=String.class, required=false)
    private String number;
    
    public static final int LENGTH_BRANCH = 10;
    @Column(name="BRANCH", length = LENGTH_BRANCH, nullable = true)
    @XmlElement(name="Branch", type=String.class, required=false)
    private String branch;

    public Phone() {
    }

    public Phone(PhoneType phoneType, String number) {
        this.phoneType = phoneType;
        this.number = number;
    }

    public Phone(PhoneType phoneType, String areaCode, String number, String branch) {
        this.phoneType = phoneType;
        this.areaCode = areaCode;
        this.number = number;
        this.branch = branch;
    }
    
    // Used for converting String values to object Phone
    public Phone(String value, String branchname) {
        parse(value, branchname);
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.phoneType);
        hash = 71 * hash + Objects.hashCode(this.areaCode);
        hash = 71 * hash + Objects.hashCode(this.number);
        hash = 71 * hash + Objects.hashCode(this.branch);
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
        final Phone other = (Phone) obj;
        if (this.phoneType != other.phoneType) {
            return false;
        }
        if (!Objects.equals(this.areaCode, other.areaCode)) {
            return false;
        }
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        if (!Objects.equals(this.branch, other.branch)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<Phone>");
        // Phone Type
        builder.append("<Type>").append(phoneType != null ? 
              phoneType.toString() : "NULL").append("</Type>");
        // Area Code
        builder.append("<AreaCode>").append(areaCode != null ?
                areaCode : "NULL").append("</AreaCode>");
        // Number
        builder.append("<Number>").append(number != null ?
                number : "NULL").append("</Number>");
        // Branch
        builder.append("<Branch>").append(branch != null ?
                branch : "NULL").append("</Branch>");
        
        builder.append("</Phone>");
        
        return builder.toString();
    }

    public Phone parse(String value, String branchname) {
        setPhoneType(null);
        setAreaCode(null);
        setNumber(null);
        setBranch(null);
        
        // Is there a Type ?
        int pos = 0, tmp = 0;
        for (PhoneType phoneType : PhoneType.values()) {
            pos = value.indexOf(phoneType.toString());
            if (pos >= 0) {// Found it 
//                type = EXAMPLE.substring(pos, EXAMPLE.indexOf(SEPARATOR_TYPE, pos + 1));
                setPhoneType(phoneType);
                pos = phoneType.toString().length() + SEPARATOR_TYPE.length();
                break;
            }
        }
//        System.out.printf("Phone Type:|%s|\n", type);
//        System.out.printf("#1 pos:%d\n", pos);

        // Is there any Area Code ?
        tmp = value.indexOf(SEPARATOR_START_AREA_CODE);
        pos = tmp >= 0 ? tmp : pos;
        if (tmp >= 0) {
            setAreaCode(value.substring(pos+1, value.indexOf(SEPARATOR_END_AREA_CODE, pos + 1)));
            pos = value.indexOf(SEPARATOR_END_AREA_CODE, pos + 1) + 1;
        }
//        System.out.printf("#2 pos:%d tmp:%d\n", pos, tmp);

        // Is there a branch ?
        tmp = value.indexOf(branchname);
        if (tmp >= 0) {
            setBranch(value.substring(tmp + SEPARATOR_TYPE.length() + 
                                branchname.length(), value.length()).trim());
        }
//        System.out.printf("Branch: |%s|\n", branch);

        // Number ?
//        System.out.printf("#3 pos:%d tmp:%d\n", pos, tmp);
        setNumber(value.substring(pos >= 0 ? pos : 0, tmp >= 0 ? tmp : value.length()).trim());
        if (getNumber().length() == 0) {
            setNumber(null);
        }
//        System.out.printf("Number: |%s|\n", number);
        
        return this;
    }
    
    
    public String toString(String branchname) {
        StringBuilder builder = new StringBuilder();

        // Phone Type
        if(phoneType != null && (areaCode != null || number != null || branch != null)) {
            builder.append(phoneType.toString());
            builder.append(SEPARATOR_TYPE);
        }
        
        // Area Code
        if(areaCode != null && !areaCode.isEmpty()) {
            builder.append(" ");
            builder.append(SEPARATOR_START_AREA_CODE);
            builder.append(areaCode);
            builder.append(SEPARATOR_END_AREA_CODE);
        }
        
        // Phone Number
        if(number != null && !number.isEmpty()) {
            builder.append(" ");
            builder.append(number);
        }
        
        // Branch
        if(branch != null && !branch.isEmpty()) {
            builder.append(" ");
            builder.append(branchname);
            builder.append(SEPARATOR_TYPE);
            builder.append(" ");
            builder.append(branch);
        }
        
        return builder.toString().trim();
    }    
}
