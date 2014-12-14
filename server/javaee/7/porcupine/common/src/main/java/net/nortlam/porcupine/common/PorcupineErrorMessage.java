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

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author Mauricio "Maltron" Leal */
@XmlRootElement(name="ErrorMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class PorcupineErrorMessage implements Serializable {
    
    @XmlElement(name="Type", type=String.class, required=true)
    private String type;
    
    @XmlElement(name="Parameters", type=String.class, required=true)
    private String parameters;
    
    @XmlElement(name="Specific", type=String.class, required=true)
    private String specific;

    public PorcupineErrorMessage() {
    }

    public PorcupineErrorMessage(String type, String parameters, String specific) {
        this.type = type;
        this.parameters = parameters;
        this.specific = specific;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getSpecific() {
        return specific;
    }

    public void setSpecific(String specific) {
        this.specific = specific;
    }
}
