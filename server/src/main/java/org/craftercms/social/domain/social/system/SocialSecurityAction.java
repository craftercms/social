package org.craftercms.social.domain.social.system;

import java.util.Set;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.Document;
import org.jongo.marshall.jackson.oid.Id;

/**
 */
@Document(collectionName = "securityActions")
public class SocialSecurityAction {

    @Id
    private ObjectId id;

    private String actionName;

    private Set<String> roles;

    private String tenantId;

    public SocialSecurityAction() {
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(final String actionName) {
        this.actionName = actionName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(final Set<String> roles) {
        this.roles = roles;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }
}

