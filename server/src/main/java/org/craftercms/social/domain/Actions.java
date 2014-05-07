package org.craftercms.social.domain;

import java.util.List;
import java.util.Map;

import org.jongo.marshall.jackson.oid.Id;

/**
 * Represents a Security Profile for UGCs
 */
public class Actions {

    public static final String OWNER_ROLE="OWNER";

    @Id
    private String id;

    private String name;
    private Map<String,List<String>> actions;
    private boolean isDefault;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Map<String, List<String>> getActions() {
        return actions;
    }

    public void setActions(final Map<String, List<String>> actions) {
        this.actions = actions;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(final boolean isDefault) {
        this.isDefault = isDefault;
    }
}
