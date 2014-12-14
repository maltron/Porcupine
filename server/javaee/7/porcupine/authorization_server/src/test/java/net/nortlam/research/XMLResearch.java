package net.nortlam.research;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Objects;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class XMLResearch {
    
    public XMLResearch() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testXMLAttribute() {
        Element myElement = new Element(Options.ONE, "First");
        Writer writer = new CharArrayWriter();
        try {
            JAXBContext.newInstance(Element.class).createMarshaller().marshal(myElement, writer);
        } catch(JAXBException e) {
            System.err.printf("### JAXB EXCEPTION:%s\n", e.getMessage());
            e.printStackTrace();
        } finally {
            if(writer != null) try{writer.close();}catch(IOException e){}
        }
        
        System.out.printf(">>> Result:%s\n", writer.toString());
        
    }
}
