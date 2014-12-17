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
package net.nortlam.porcupine.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Mauricio "Maltron" Leal */
@XmlType(name="Grant")
@XmlEnum
public enum Grant {
    
    @XmlEnumValue("AuthorizationCode") AUTHORIZATION_CODE("AUTHORIZATION_CODE"),
    @XmlEnumValue("Implicit") IMPLICIT("IMPLICIT"),
    @XmlEnumValue("ResourceOwnerPasswordCredentials") RESOURCE_OWNER_PASSWORD_CREDENTIALS("RESOURCE_OWNER_PASSWORD_CREDENTIALS"),
    @XmlEnumValue("ClientCredentials") CLIENT_CREDENTIALS("CLIENT_CREDENTIALS"),
    @XmlEnumValue("RefreshToken") REFRESH_TOKEN("REFRESH_TOKEN"),
    @XmlEnumValue("Extension") EXTENSION("EXTENSION");
    
    private String value;
    
    Grant(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
