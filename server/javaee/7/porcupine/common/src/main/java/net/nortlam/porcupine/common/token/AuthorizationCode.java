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
package net.nortlam.porcupine.common.token;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlElement;
import net.nortlam.porcupine.common.entity.Client;
import net.nortlam.porcupine.common.entity.Scope;
import net.nortlam.porcupine.common.IDFactory;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.Porcupine;

/**
 * Authorization Code is a class with information regarding
 * the 2nd step from Authorization Code Grant. 
 * It encapsulates all the information needed to go 
 * to the final stage, which it's: provide the Access Token
 * 
 * @author Mauricio "Maltron" Leal */
@Entity(name="AuthorizationCode")
@Table(name="PORCUPINE_AUTHORIZATION_CODE")
@NamedQueries({
    @NamedQuery(name=AuthorizationCode.FIND_BY_CODE, query="SELECT a FROM AuthorizationCode a WHERE a.code=:CODE")
})
public class AuthorizationCode implements Serializable {

    private static final Logger LOG = Logger.getLogger(AuthorizationCode.class.getName());
    
    public static final String FIND_BY_CODE = "AuthorizationCode.findByCode()";
    
    public static final int LENGTH_CODE = 88;
    
    @Id
    @Column(name="CODE", length = LENGTH_CODE, columnDefinition = "CHAR(88)", nullable = false)
    private String code;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="CLIENT_ID",
            foreignKey = @ForeignKey(name="AUTHORIZATION_CODE_RELATED_TO_CLIENT"))
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="SCOPE_ID", 
            foreignKey = @ForeignKey(name = "AUTHORIZATION_CODE_RELATED_TO_SCOPE"))
    private Scope scope;
    
    public static final int LENGTH_REDIRECT_URI = 255;
    @Column(name="REDIRECT_URI", length = LENGTH_REDIRECT_URI, nullable = false)
    private String redirectURI;
    
    @Column(name="EXPIRATION", nullable = false) @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name="Expiration", type=Date.class, required=true)
    private Date expiration; // Default value used for each Access Token expiration 

    public AuthorizationCode() {
    }

    public AuthorizationCode(Client client, Scope scope, String redirectURI, Date expiration) {
        this.code = IDFactory.getInstance().newAuthorizationCode();
        this.client = client;
        this.scope = scope;
        this.redirectURI = redirectURI;
        this.expiration = expiration;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
    
    public boolean isExpired(ServletContext context) {
        TimeZone zone = InitParameter.parameterTimeZone(context);
        Locale locale = InitParameter.parameterLocale(context);

        return getExpiration().getTime() < 
                Calendar.getInstance(zone, locale).getTime().getTime();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.code);
        hash = 59 * hash + Objects.hashCode(this.client);
        hash = 59 * hash + Objects.hashCode(this.scope);
        hash = 59 * hash + Objects.hashCode(this.redirectURI);
        hash = 59 * hash + Objects.hashCode(this.expiration);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuthorizationCode other = (AuthorizationCode) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.client, other.client)) {
            return false;
        }
        if (!Objects.equals(this.scope, other.scope)) {
            return false;
        }
        if (!Objects.equals(this.redirectURI, other.redirectURI)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<AuthorizationCode code=\"")
                            .append(code != null ? code : "NULL").append("\">");
        builder.append("<Client>").append(client != null ? 
                                client.getID() : "NULL").append("</Client>");
        builder.append("<Scope>").append(scope != null ? 
                                scope.getID() : 0).append("</Scope>");
        builder.append("<Expiration>").append(expiration != null ? 
                new SimpleDateFormat(Porcupine.DEFAULT_DATE_FORMAT)
                        .format(expiration) : "NULL")
                .append("</Expiration>");
        builder.append("<RedirectURI>").append(redirectURI != null ? 
                redirectURI : "NULL").append("</RedirectURI>");
        
        return builder.toString();
    }
}
