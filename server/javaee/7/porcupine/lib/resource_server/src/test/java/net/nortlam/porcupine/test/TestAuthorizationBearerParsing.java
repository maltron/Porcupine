/*
 * 
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
 * 
 */
package net.nortlam.porcupine.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.nortlam.porcupine.common.token.AccessToken;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mauricio
 */
public class TestAuthorizationBearerParsing {
    
    private static final Logger LOG = Logger.getLogger(TestAuthorizationBearerParsing.class.getName());
    
    @Test
    public void test1() {
        String authorization = "Bearer adasdasdasasdad";
        AccessToken accessToken = new AccessToken();
        try {
            accessToken.parseAuthorizationBearer(authorization);
            assertEquals(accessToken.getToken(), "adasdasdasasdad");
            LOG.log(Level.INFO, "111111 Token:{0}", accessToken.getToken());
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "ILLEGAL ARGUMENT EXCEPTION:{0}", ex.getMessage());
        }
    }
    
    @Test
    public void test2() {
        String authorization = "Bearer adasdasdasasdad,";
        AccessToken accessToken = new AccessToken();
        try {
            accessToken.parseAuthorizationBearer(authorization);
            assertEquals(accessToken.getToken(), "adasdasdasasdad");
            LOG.log(Level.INFO, "222222 Token:{0}", accessToken.getToken());
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "ILLEGAL ARGUMENT EXCEPTION:{0}", ex.getMessage());
        }
    }
    
    @Test
    public void test3() {
        String authorization = "Bearer cG9yY3VwaW5lLWFjY2Vzcy10b2tlbi00MzRiM2Q3MC1hYzE3LTRiYmMtOWE5Ni1mYjAxMjRkMjVkZTk=, Basic sdfsdfs";
        AccessToken accessToken = new AccessToken();
        try {
            accessToken.parseAuthorizationBearer(authorization);
            assertEquals(accessToken.getToken(), "cG9yY3VwaW5lLWFjY2Vzcy10b2tlbi00MzRiM2Q3MC1hYzE3LTRiYmMtOWE5Ni1mYjAxMjRkMjVkZTk=");
            LOG.log(Level.INFO, "333333 Token:{0}", accessToken.getToken());
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "ILLEGAL ARGUMENT EXCEPTION:{0}", ex.getMessage());
        }
    }
    
    @Test
    public void test4() {
        String authorization = "Basic sdfsdfs,Bearer cG9yY3VwaW5lLWFjY2Vzcy10b2tlbi00MzRiM2Q3MC1hYzE3LTRiYmMtOWE5Ni1mYjAxMjRkMjVkZTk=";
        AccessToken accessToken = new AccessToken();
        try {
            accessToken.parseAuthorizationBearer(authorization);
            assertEquals(accessToken.getToken(), "cG9yY3VwaW5lLWFjY2Vzcy10b2tlbi00MzRiM2Q3MC1hYzE3LTRiYmMtOWE5Ni1mYjAxMjRkMjVkZTk=");
            LOG.log(Level.INFO, "444444 Token:{0}", accessToken.getToken());
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "ILLEGAL ARGUMENT EXCEPTION:{0}", ex.getMessage());
        }
    }

    @Test
    public void test5() {
        String authorization = "Basic sdfsdfs,   Bearer          cG9yY3VwaW5lLWFjY2Vzcy10b2tlbi00MzRiM2Q3MC1hYzE3LTRiYmMtOWE5Ni1mYjAxMjRkMjVkZTk=, SOMETHING ELSE";
        AccessToken accessToken = new AccessToken();
        try {
            accessToken.parseAuthorizationBearer(authorization);
            assertEquals(accessToken.getToken(), "cG9yY3VwaW5lLWFjY2Vzcy10b2tlbi00MzRiM2Q3MC1hYzE3LTRiYmMtOWE5Ni1mYjAxMjRkMjVkZTk=");
            LOG.log(Level.INFO, "55555 Token:{0}", accessToken.getToken());
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "ILLEGAL ARGUMENT EXCEPTION:{0}", ex.getMessage());
        }
    }

    @Test
    public void test6() {
        String authorization = "Basic sdfsdfs,   Bearer , SOMETHING ELSE";
        AccessToken accessToken = new AccessToken();
        try {
            accessToken.parseAuthorizationBearer(authorization);
            assertEquals(accessToken.getToken(), "");
            LOG.log(Level.INFO, "66666 Token:{0}", accessToken.getToken());
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "ILLEGAL ARGUMENT EXCEPTION:{0}", ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test7() {
        String authorization = "Basic sdfsdfs, SOMETHING ELSE";
        AccessToken accessToken = new AccessToken();
        accessToken.parseAuthorizationBearer(authorization);
    }

    @Test
    public void test8() {
        String authorization = "Basic sdfsdfs, SOMETHING Bearer x, ELSE";
        AccessToken accessToken = new AccessToken();
        accessToken.parseAuthorizationBearer(authorization);
        assertEquals(accessToken.getToken(), "x");
        LOG.log(Level.INFO, "88888 Token:{0}", accessToken.getToken());
    }
    
    @Test
    public void test9() {
        String authorization = "Basic sdfsdfs, SOMETHING Bearer x ELSE";
        AccessToken accessToken = new AccessToken();
        accessToken.parseAuthorizationBearer(authorization);
        assertEquals(accessToken.getToken(), "x ELSE");
        LOG.log(Level.INFO, "99999 Token:{0}", accessToken.getToken());
    }
}
