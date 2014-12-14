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
package net.nortlam.porcupine.authorization.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import net.nortlam.porcupine.common.OAuth2;
import net.nortlam.porcupine.common.exception.ServerErrorException;
import net.nortlam.porcupine.common.token.AccessToken;
import net.nortlam.porcupine.common.token.AuthorizationCode;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Stateless
public class MessagingService implements Serializable {
    
    public static final String QUEUE = "java:/jms/queue/PorcupineQueue";
    public static final long AUTHORIZATION_CODE_EXPIRATION = 10000; // 10 s
    public static final long DEFAULT_TIMEOUT = 1000; // 1s para timeout
    
    @Inject
    private JMSContext context;
    
    @Resource(mappedName=QUEUE)
    private Queue queue;

    private static final Logger LOG = Logger.getLogger(MessagingService.class.getName());

    public MessagingService() {
    }
    
    public void storeCode(OAuth2 oauth, AuthorizationCode code) throws ServerErrorException {
        context.createProducer()
                .setDeliveryMode(DeliveryMode.PERSISTENT)
                .setTimeToLive(AUTHORIZATION_CODE_EXPIRATION)
                .setProperty(OAuth2.PARAMETER_CODE, code.getCode())
                .send(queue, code);
        
//        try(Connection connection = factory.createConnection()) {
//            connection.start();
//            
//            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//            MessageProducer producer = session.createProducer(queue);
//            // The Code won't last forever
//            producer.setTimeToLive(AUTHORIZATION_CODE_EXPIRATION);
//            // In case of Server failure
//            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
//            
//            // Builds a message
//            // PROPERTY: code=xyzxyzxyz
//            // BODY:     clientID
////            TextMessage message = session.createTextMessage(clientID);
//            ObjectMessage message = session.createObjectMessage(code);
//            // Use this property. It will be used to select to the next step
//            message.setStringProperty(OAuth2.PARAMETER_CODE, code.getCode());
//            
//            // Send to the Queue
//            producer.send(message);
//            
//        } catch(JMSException ex) {
//            LOG.log(Level.SEVERE,"### MessagingService.storeCode() JMS EXCEPTION:{0}", ex.getMessage());
//            throw new ServerErrorException(oauth);
//        }
    }
    
    public AuthorizationCode restoreCode(OAuth2 oauth, String code) throws ServerErrorException {
        String selector = String.format("%s=\'%s\'", OAuth2.PARAMETER_CODE, code);
        return context.createConsumer(queue, selector)
                .receiveBody(AuthorizationCode.class, DEFAULT_TIMEOUT);
        
//        AuthorizationCode authorizationCode = null;
//        try(Connection connection = factory.createConnection()) {
//            connection.start();
//            
//            // Selector (or query) to get the 
//            String selector = String.format("%s=\'%s\'", OAuth2.PARAMETER_CODE, code);
//            
//            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//            MessageConsumer consumer = session.createConsumer(queue, selector);
//            
//            // Fetch the message based on the selector
//            Message message = consumer.receive(DEFAULT_TIMEOUT);
//            // It should return the client_id
//            // NULL means it couldn't find it or it has expired it 
//            authorizationCode = message.getBody(AuthorizationCode.class); 
//            
//        } catch(JMSException ex) {
//            LOG.log(Level.SEVERE,"### MessagingService.restoreCode() JMS EXCEPTION:{0}", ex.getMessage());
//            throw new ServerErrorException(oauth);
//        }
//        
//        return authorizationCode;
    }
    
    public void storeAccessToken(AccessToken accessToken) throws ServerErrorException {
        context.createProducer()
                .setDeliveryMode(DeliveryMode.PERSISTENT)
                .setTimeToLive(accessToken.getExpiration().getTime())
                // 2 Ways to Search: Access Token and Refresh Token
                .setProperty(OAuth2.PARAMETER_ACCESS_TOKEN, accessToken.getToken())
                .setProperty(OAuth2.PARAMETER_REFRESH_TOKEN, accessToken.getRefreshToken())
                .send(queue, accessToken);
    }

    /**
     * Used to retrieve both AccessToken and RefreshToken */
    public AccessToken retrieveAccessToken(OAuth2 oauth, String parameter, String token) 
                                                            throws ServerErrorException {
        AccessToken accessToken = null;
        try {
            String selector = String.format("%s=\'%s\'", parameter, token);
            Message message = context
                .createConsumer(queue, selector).receive(DEFAULT_TIMEOUT); // Acknowledges the message
        
            // Does Access Token still exists ? Then send back to the Queue with the remaining time
            if(message != null) {
                accessToken = message.getBody(AccessToken.class);
                // The remaining time
                long remaining = message.getJMSExpiration() - Calendar.getInstance().getTime().getTime();
                // Update the remaining time inside the AccessToken
                accessToken.setExpiration(new Date(remaining));
                // Sending back with the remaining time
                storeAccessToken(accessToken);
            }
            
        } catch(JMSException ex) {
            LOG.log(Level.SEVERE, "### MessagingService.retrieveAccessToken() JMS EXCEPTION:{0}", ex.getMessage());
            throw new ServerErrorException(oauth);
        }
        
        return accessToken;
    }
}
