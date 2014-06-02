package org.craftercms.social.repositories;

import org.craftercms.social.domain.UGC;

/**
 *
 */
public interface UgcFactory<T extends UGC> {

    Class<T> getMainClass();
    Class<?> getTreeClass();
    T newInstance(T base);
}
