package org.craftercms.social.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.bson.types.ObjectId;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;

/**
 */
public class SocialSecurityUtils {

    public static final String ANONYMOUS_ROLE = "ANONYMOUS_ROLE";
    public static final String ANONYMOUS = "ANONYMOUS";

    private SocialSecurityUtils(){}


    public static Profile getCurrentProfile() {
        Authentication currentAuth = SecurityUtils.getCurrentAuthentication();
        Profile profile;
        if(currentAuth==null){
            profile=new Profile();
            profile.setUsername(ANONYMOUS);
            profile.setUsername(ANONYMOUS_ROLE);
            profile.setRoles(new LinkedHashSet<>(Arrays.asList(ANONYMOUS_ROLE)));
            profile.setAttributes(new HashMap<String, Object>());
            profile.setId(new ObjectId("53a8c138b91d36008c85714f"));
            profile.setTenant("test");
        }else{
            profile=currentAuth.getProfile();
        }
        return profile;
    }
}
