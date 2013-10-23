package org.craftercms.social.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("profile")
@XmlRootElement
@CompoundIndexes({
        @CompoundIndex(name = "tenantName_userName_idx", def = "{'tenantName': 1, 'userName': 1}", unique = true),
        @CompoundIndex(name = "tenantName_idx", def = "{'tenantName': 1}" )
})
@JsonIgnoreProperties({ "password" })
public class Profile implements Serializable {
    private static final long serialVersionUID = 3370284215738389717L;
    
    public static final String FIELD_ID = "_id";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String VERIFY = "verify";
    public static final String VERIFY_ACCOUNT_URL = "verificationAccountUrl";
    public static final String ACTIVE = "active";
    public static final String CREATED = "created";
    public static final String MODIFIED = "modified";
    public static final String ROLES = "roles";
    public static final String DOMAINS = "domains";
    public static final String ATTRIBUTES = "attributes";
    public static final String TENANT_NAME = "tenantName";

    @Field(FIELD_ID)
    private ObjectId id;

    @Field(USER_NAME)
    private String userName;
    
    @Field(PASSWORD)
    @XmlTransient
    private String password;

    @Field(ACTIVE)
    private Boolean active;

    @Field(CREATED)
    private Date created;

    @Field(MODIFIED)
    private Date modified;

    @Field(TENANT_NAME)
    private String tenantName;

    @Field(EMAIL)
    private String email;
    
    @Field(VERIFY)
    private Boolean verify;

    @Field(ROLES)
    private List<String> roles;

    @Field(ATTRIBUTES)
    private Map<String, Serializable> attributes;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlTransient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Boolean getVerify() {
        return verify;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Map<String, Serializable> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Serializable> attributes) {
        this.attributes = attributes;
    }

    public String toString() {
        return String.format("Profile [id='%s' userName='%s' active='%b' email='%s'created='%tc' " +
            "modified='%tc']", id, userName, active, email, created, modified);
    }

    public String getTenantName() {
        return tenantName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
