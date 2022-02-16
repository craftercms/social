/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
