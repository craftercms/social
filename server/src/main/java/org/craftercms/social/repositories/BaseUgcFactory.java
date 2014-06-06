package org.craftercms.social.repositories;

import org.craftercms.social.domain.UGC;

/**

 */
public class BaseUgcFactory implements TreeUGC<UGC> {

    private UGC ugc;

    @Override
    public UGC getUGC() {
        return ugc;
    }
}
