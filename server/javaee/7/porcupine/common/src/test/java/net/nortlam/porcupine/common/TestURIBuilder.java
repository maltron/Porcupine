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
import java.net.URI;
import java.util.logging.Logger;
import net.nortlam.porcupine.common.util.URIBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author Mauricio "Maltron" Leal */
public class TestURIBuilder implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestURIBuilder.class.getName());

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
    public void testBuildingParameters() {
        String value = URIBuilder.buildParameters("a", "a", "b", "b");
        System.out.printf(">>> Value:%s\n", value);
    }
    
    @Test
    public void testBuildURI() {
        URI uri = URIBuilder.buildURI("http", "localhost", 8080, "/server/oauth2/authorize", 
                OAuth2.PARAMETER_RESPONSE_TYPE, OAuth2.PARAMETER_RESPONSE_TYPE_CODE,
                OAuth2.PARAMETER_CLIENT_ID, "cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU",
                OAuth2.PARAMETER_SCOPE, "EMAIL",
                OAuth2.PARAMETER_REDIRECT_URI, "http://localhost:8080/client/rest/person");
        System.out.printf(">>> URI:%s\n", uri.toString());
    }
    
    @Test
    public void testBuildURIWithNull() {
        String value = URIBuilder.buildParameters("a", "1", "b", "2", "c", null);
        System.out.printf(">>> testBuildURIWithNull() Value:%s\n", value);
        
        URI uri = URIBuilder.buildURI("http", "localhost", 8080, "/server/oauth2/authorize");
        System.out.printf(">>> testBUildURIWithNull() NO PARAMETERS: %s\n",uri.toString());
    }
    
    
    

}
