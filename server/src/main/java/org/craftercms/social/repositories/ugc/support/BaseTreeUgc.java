package org.craftercms.social.repositories.ugc.support;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.repositories.TreeUGC;

/**
*
*/
public class BaseTreeUgc<T extends UGC> implements TreeUGC<T> {

    private UGC ugc;

    public BaseTreeUgc() {
    }

    @Override
    public T getUGC() {
        return (T)ugc;
    }
}
