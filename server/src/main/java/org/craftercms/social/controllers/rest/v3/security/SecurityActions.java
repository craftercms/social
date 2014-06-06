package org.craftercms.social.controllers.rest.v3.security;

import java.util.Set;

import org.craftercms.commons.mongo.Document;
import org.jongo.marshall.jackson.oid.Id;

/**
 */
@Document(collectionName = "securityActions")
public class SecurityActions {

    @Id
    private String action;

    private Set<String> roles;

    public SecurityActions() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(final Set<String> roles) {
        this.roles = roles;
    }
}
