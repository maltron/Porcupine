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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import net.nortlam.porcupine.common.IDFactory;
import net.nortlam.porcupine.common.InitParameter;
import net.nortlam.porcupine.common.entity.Scope;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Entity(name="AccessToken")
@Table(name="PORCUPINE_ACCESS_TOKEN")
@NamedQueries({
    @NamedQuery(name=AccessToken.FIND_BY_TOKEN, query="SELECT access_token FROM AccessToken access_token WHERE access_token.token=:TOKEN"),
    @NamedQuery(name=AccessToken.FIND_BY_REFRESH_TOKEN, 
            query="SELECT access_token FROM AccessToken access_token WHERE access_token.refreshToken=:REFRESH_TOKEN")
})
@XmlRootElement(name="AccessToken")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"token", "tokenType", "expiration", "refreshToken", "exampleParameter"})
public class AccessToken implements Serializable {

    private static final Logger LOG = Logger.getLogger(AccessToken.class.getName());
    
    public static final String FIND_BY_TOKEN = "AccessToken.findByToken()";
    public static final String FIND_BY_REFRESH_TOKEN = "AccessToken.findByRefreshToken()";
    
    public static final String TOKEN_TYPE_BEARER = "bearer";
    public static final String AUTHORIZATION_BEARER = "Bearer";
    
    public static final int LENGTH_TOKEN = 80;
    
    @Id
    @Column(name="TOKEN", length = LENGTH_TOKEN, columnDefinition = "CHAR(80)", nullable = false)
    @XmlElement(name="access_token", type=String.class, required=true)
    private String token;
    
    public static final int LENGTH_TOKEN_TYPE = 20;
    
    @Column(name="TOKEN_TYPE", length = LENGTH_TOKEN_TYPE, nullable = false)
    @XmlElement(name="token_type", type=String.class, required=true)
    private String tokenType; // So far, let's use the name of the Scope used
    
    @Column(name="EXPIRATION", nullable = false) @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name="Expiration", type=Date.class, required=true)
    private Date expiration; // Default value used for each Access Token expiration 

    public static final int LENGTH_REFRESH_TOKEN = 80;
    @Column(name="REFRESH_TOKEN", length = LENGTH_REFRESH_TOKEN, 
            columnDefinition = "CHAR(80)", nullable = true, unique = true)
    @XmlElement(name="refresh_token", type=String.class, required=false)
    private String refreshToken;
    
    public static final int LENGTH_EXAMPLE_PARAMETER = 20;
    @Column(name="EXAMPLE_PARAMETER", length = LENGTH_EXAMPLE_PARAMETER, nullable = false)
    @XmlElement(name="example_parameter", type=String.class, required=true)
    private String exampleParameter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="SCOPE_ID", nullable = false, 
            foreignKey = @ForeignKey(name = "ACCESS_TOKEN_TO_A_SCOPE")) // Porcupine: REQUIRED
    @XmlTransient
    private Scope scope; // Through Scope, We do know each Protected Resources to protect
    
    public AccessToken() {
    }
    
    public AccessToken(ServletContext context, AuthorizationCode code) {
        this(IDFactory.getInstance().newAccessToken(), TOKEN_TYPE_BEARER, 
                // Used Scopes to give the Expiration Timming
                code.getScope().getExpirationTimming(context), 
                IDFactory.getInstance().newRefreshToken(), 
                "example", code.getScope());
        // PENDING: NEED TO CHECK ABOUT THE RESOURCES PURPORSES
        // MAYBE, SOMETHING RELATED TO ProtectedResources ????
    }
    
    /**
     * Creating a new Access Token, based on a old Access Token expired */
    public AccessToken(ServletContext context, AccessToken oldAccessToken) {
        this(IDFactory.getInstance().newAccessToken(), 
                oldAccessToken.getTokenType(), 
                oldAccessToken.getScope().getExpirationTimming(context),
                IDFactory.getInstance().newRefreshToken(), "example", 
                oldAccessToken.getScope());
    }

    public AccessToken(ServletContext context, Scope scope) {
        this(IDFactory.getInstance().newAccessToken(), TOKEN_TYPE_BEARER,
                scope.getExpirationTimming(context), 
                IDFactory.getInstance().newRefreshToken(), "example", 
                scope);
    }

    public AccessToken(String token, String tokenType, Date expiration, 
            String refreshToken, String exampleParameter, Scope scope) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiration = expiration;
        this.refreshToken = refreshToken;
        this.exampleParameter = exampleParameter;
        this.scope = scope;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Date getExpiration() {
        return expiration;
    }
    
    public String getExpirationInSeconds() {
        if(getExpiration() == null) return null;
        
        return String.valueOf(getExpiration().getTime()/1000);
    }
    
    public void setExpirationInSeconds(String value) {
        if(value != null) 
            setExpiration(new Date(Long.parseLong(value)*1000));
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
    
    public boolean hasRefreshToken() {
        return getRefreshToken() != null;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExampleParameter() {
        return exampleParameter;
    }

    public void setExampleParameter(String exampleParameter) {
        this.exampleParameter = exampleParameter;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
    
    public String toStringAuthorizationBearer() {
        if(getToken() == null) return null;
        
        return String.format("%s %s", AUTHORIZATION_BEARER, getToken());
    }
    
    public List<String> toListAuthorizationBearer() {
        if(getToken() == null) return null;
        
        return Arrays.asList(toStringAuthorizationBearer());
    }
    
    public AccessToken parseAuthorizationBearer(String value) throws IllegalArgumentException {
        if(value == null) throw new IllegalArgumentException(
                "### parseAuthorizationBearer() Value is *NULL*");
  
        int bearer = value.indexOf(AUTHORIZATION_BEARER);
        if(bearer < 0) // There isn't the Bearer indicator
            throw new IllegalArgumentException("### parseAuthorizationBearer() "+
                    " Unable to locate the <Bearer> indicator");
        int start = bearer+AUTHORIZATION_BEARER.length();
        int end = value.indexOf(",", start);
        if(end < 0) end = value.length();
        
        setToken(value.substring(start, end).trim());
        
        return this;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.token);
        hash = 37 * hash + Objects.hashCode(this.tokenType);
        hash = 37 * hash + Objects.hashCode(this.expiration);
        hash = 37 * hash + Objects.hashCode(this.refreshToken);
        hash = 37 * hash + Objects.hashCode(this.exampleParameter);
        hash = 37 * hash + Objects.hashCode(this.scope);
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
        final AccessToken other = (AccessToken) obj;
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        if (!Objects.equals(this.tokenType, other.tokenType)) {
            return false;
        }
        if (this.expiration != other.expiration) {
            return false;
        }
        if (!Objects.equals(this.refreshToken, other.refreshToken)) {
            return false;
        }
        if (!Objects.equals(this.exampleParameter, other.exampleParameter)) {
            return false;
        }
        if (!Objects.equals(this.scope, other.scope)) {
            return false;
        }
        
        return true;
    }
}
