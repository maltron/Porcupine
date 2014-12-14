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

import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.token.AuthorizationCode;

/**
 * This general interfaces means that another way to store the codes may be used
 * for performance/security/high availability purposes 
 *
 * @author Mauricio "Maltron" Leal */
public interface TokenManagement {
    
    public void storeCode(OAuth2 oauth, AuthorizationCode code);
    public AuthorizationCode restoreCode(OAuth2 oauth, String code);
    public void deleteCode(OAuth2 auth, AuthorizationCode code);
    
    public void storeAccessToken(OAuth2 oauth, AuthorizationCode code, AccessToken accessToken);
    public void storeAccessToken(OAuth2 oauth, AccessToken accessToken);
    public AccessToken retrieveAccessToken(OAuth2 oauth, String token);
    public void deleteAccessToken(OAuth2 oauth, AccessToken accessToken);
    
    public AccessToken retrieveAccessTokenFromRefreshToken(OAuth2 oauth, String refreshToken);
    public void refreshingAccessToken(OAuth2 oauth, AccessToken oldAccessToken, 
                                                        AccessToken newAccessToken);
}
