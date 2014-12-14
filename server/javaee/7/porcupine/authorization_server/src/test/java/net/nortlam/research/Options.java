/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nortlam.research;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Mauricio "Maltron" Leal
 */
@XmlEnum
@XmlType(name = "Options")
public enum Options {

    @XmlEnumValue("One") ONE,
    @XmlEnumValue("Two") TWO,
    @XmlEnumValue("Three") THREE
}
