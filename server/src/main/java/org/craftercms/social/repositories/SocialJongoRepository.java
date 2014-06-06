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
