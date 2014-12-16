package net.nortlam.porcupine.test;

import javax.ws.rs.core.UriBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Mauricio "Maltron" Leal */
public class TestRequestingURI {

    @Test
    public void testQueryParameters() {
        String clientID = "cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU";
        
        UriBuilder builder = UriBuilder.fromUri("http://localhost:8080/server");
        builder.queryParam("client_id", clientID);
        System.out.printf("URL: %s\n",builder.build());
        
        assertEquals("client_id="+clientID, builder.build().getQuery());
    }
}
