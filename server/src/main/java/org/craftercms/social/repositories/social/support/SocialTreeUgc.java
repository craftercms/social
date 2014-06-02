package org.craftercms.social.repositories.social.support;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.repositories.TreeUGC;

/**
*
*/
public class SocialTreeUgc implements TreeUGC<SocialUgc>{

    private SocialUgc ugc;

    @Override
    public SocialUgc getUGC() {
        return ugc;
    }
}
