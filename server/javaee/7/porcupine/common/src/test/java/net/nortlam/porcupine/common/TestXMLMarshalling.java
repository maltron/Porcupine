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

import java.io.CharArrayWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import net.nortlam.porcupine.common.token.AccessToken;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author Mauricio "Maltron" Leal */
public class TestXMLMarshalling implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestXMLMarshalling.class.getName());

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
    public void testMarshalingAccessCode() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, 1000*60);
        
        AccessToken accessToken = new AccessToken(
                IDFactory.getInstance().newAccessToken(),"EMAIL", calendar.getTime(), 
                IDFactory.getInstance().newRefreshToken(), "example", null);
        LOG.log(Level.INFO, ">>> testMarshalingAccessCode() {0}", marshaling(accessToken, AccessToken.class));
    }
    
    private String marshaling(Object object, Class clazz) {
        Writer writer = new CharArrayWriter();
        try {
            JAXBContext.newInstance(clazz).createMarshaller().marshal(object, writer);
        } catch(JAXBException ex) {
            LOG.log(Level.SEVERE, "### marshaling() JAXB EXCEPTION:{0}", ex.getMessage());
            ex.printStackTrace();
        }
        
        return writer.toString();
    }
}
