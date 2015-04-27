package org.craftercms.social.repositories.social;

import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.ModerationStatus;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.repositories.UgcFactory;
import org.craftercms.social.repositories.social.support.SocialTreeUgc;

/**
 *
 */
public class SocialUgcFactory<T extends UGC> implements UgcFactory<T> {

    @Override
    public Class getMainClass() {
        return SocialUgc.class;
    }

    @Override
    public Class<?> getTreeClass() {
        return SocialTreeUgc.class;
    }

    @Override
    public T newInstance(final T base) {
        SocialUgc socialUgc = new SocialUgc(base);
        socialUgc.setModerationStatus(ModerationStatus.UNMODERATED);
        return (T)socialUgc;
    }

}
