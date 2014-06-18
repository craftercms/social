package org.craftercms.social.controllers.rest.v3.comments;

/**
 *
 */
public enum SocialSortOrder {

    ASC(true),DESC(false);
    private  boolean value;

    private SocialSortOrder(boolean value){
        this.value=value;
    }

    public boolean value(){
        return value;
    }
}
