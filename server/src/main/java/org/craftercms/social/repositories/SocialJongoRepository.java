/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.repositories;

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.social.domain.UGC;

/**
 *
 */
public class SocialJongoRepository<T extends UGC> extends AbstractJongoRepository<T> {

    protected UgcFactory ugcFactory;

    /**
     * Creates a instance of a Jongo Repository.
     */
    public SocialJongoRepository() throws Exception {
        //WE don't call super I't will fail due T is now more abstract and "dynamic"
    }

    @Override
    public void init() throws Exception {
        collectionName = UGC.COLLECTION_NAME; //Make sure it is always the same collection.
        clazz = (Class<? extends T>)ugcFactory.getMainClass();
    }


    public void setUgcFactory(final UgcFactory ugcFactory) {
        this.ugcFactory = ugcFactory;
    }
}
