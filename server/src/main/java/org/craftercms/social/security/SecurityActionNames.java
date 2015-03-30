package org.craftercms.social.security;

/**
 * Created by cortiz on 6/26/14.
 */
public final class SecurityActionNames {

    public static final String UGC_UPDATE = "ugc.update";
    public static final String UGC_MODERATE = "ugc.moderate";
    public static final String UGC_UNFLAG = "ugc.unflag";
    public static final String UGC_FLAG = "ugc.flag";
    public static final String UGC_CREATE = "ugc.create";
    public static final String UGC_DELETE = "ugc.delete";
    public static final String UGC_READ = "ugc.read";
    public static final String SYSTEM_GET_ALL_CONTEXTS = "system.socialctx.all";
    public static final String SYSTEM_CREATE_CONTEXT = "system.socialctx.create";
    public static final String SYSTEM_ADD_PROFILE_CONTEXT = "system.socialctx.addProfile";
    public static final String SYSTEM_REMOVE_PROFILE_CONTEXT = "system.socialctx.removeProfile";
    public static final String TEMPLATE_CONTEXT_ACTIONS = "TEMPLATE_CONTEXT_ACTIONS";
    public static final String ROLE_SOCIAL_SUPERADMIN="SOCIAL_SUPERADMIN";
    public static final String ROLE_OWNER="OWNER";
    public static final String ROLE_SOCIAL_ADMIN = "SOCIAL_ADMIN";
    public static final String CHANGE_NOTIFICATION_TEMPLATE = "system.notification.changeTemplate";

    private SecurityActionNames(){}


}