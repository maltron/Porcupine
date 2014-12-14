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
package net.nortlam.porcupine.authorization.token;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.token.AuthorizationCode;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Default @Database
@Stateless
public class TokenManagementUsingDatabase implements TokenManagement {

    private static final Logger LOG = Logger.getLogger(TokenManagementUsingDatabase.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void storeCode(OAuth2 oauth, AuthorizationCode code) {
        try {
            entityManager.persist(code);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "storeCode() EXCEPTION:{0}", ex.getMessage());
        }
    }

    @Override
    public AuthorizationCode restoreCode(OAuth2 oauth, String code) {
        try {
            return entityManager.createNamedQuery(AuthorizationCode.FIND_BY_CODE, AuthorizationCode.class)
                    .setParameter("CODE", code).getSingleResult();
        } catch (NoResultException ex) {
            LOG.log(Level.WARNING, "restoreCode() Unable to find AuthorizationCode:{0}", code);
        } 
        
        return null;
    }

    @Override
    public void deleteCode(OAuth2 auth, AuthorizationCode code) {
        AuthorizationCode existing = entityManager.find(AuthorizationCode.class, code.getCode());
        entityManager.remove(existing);
    }

    @Override
    public void storeAccessToken(OAuth2 oauth, AuthorizationCode code, AccessToken accessToken) {
        try {
            // In order to store an AccessToken, the Authorization Code must be deleted
            AuthorizationCode existing = entityManager.find(AuthorizationCode.class, code.getCode());
            entityManager.remove(existing);
            
            entityManager.persist(accessToken);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "storeAccessToken() EXCEPTION:{0}", ex.getMessage());
        }
    }
    
    @Override
    public void storeAccessToken(OAuth2 oauth, AccessToken accessToken) {
        try {
            entityManager.persist(accessToken);
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, "storeAccessToken() EXCEPTION:{0}", ex.getMessage());
        }
    }

    @Override
    public AccessToken retrieveAccessToken(OAuth2 oauth, String token) {
        try {
            return entityManager.createNamedQuery(AccessToken.FIND_BY_TOKEN, AccessToken.class)
                    .setParameter("TOKEN", token).getSingleResult();
        } catch (NoResultException ex) {
            LOG.log(Level.WARNING, "restrieveAccessToken() Unable to find Access Token:{0}",
                    token);
        }
        
        return null;
    }

    @Override
    public void deleteAccessToken(OAuth2 oauth, AccessToken accessToken) {
        AccessToken existing = entityManager.find(AccessToken.class, accessToken.getToken());
        entityManager.remove(existing);
    }
    
    @Override
    public AccessToken retrieveAccessTokenFromRefreshToken(OAuth2 oauth, String refreshToken) {
        AccessToken accessToken = null;
        try {
            accessToken = entityManager.createNamedQuery(AccessToken.FIND_BY_REFRESH_TOKEN, 
                    AccessToken.class).setParameter("REFRESH_TOKEN", refreshToken)
                    .getSingleResult();
        } catch(NoResultException ex) {
            LOG.log(Level.WARNING, "retrieveAccessTokenFromRefreshToken() "+
                    " Unable to find RefreshToken:{0}", ex.getMessage());
        }
        
        return accessToken;
    }
    
    @Override
    public void refreshingAccessToken(OAuth2 oauth, AccessToken oldAccessToken, 
                                                    AccessToken newAccessToken) {
        // First, delete the old Access Token
        entityManager.remove(entityManager.find(AccessToken.class, 
                                                    oldAccessToken.getToken()));

        // Then, persist the new Access Token
        entityManager.persist(newAccessToken);
    }
}
