package org.craftercms.social.repositories;

import com.mongodb.MongoException;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by cortiz on 4/15/14.
 */
public class SecurityProfileRepositoryImpl extends JongoRepository<Actions> implements SecurityProfileRepository {
    private Logger log = LoggerFactory.getLogger(SecurityProfileRepositoryImpl.class);
    /**
     * Creates a instance of a Jongo Repository.
     */
    public SecurityProfileRepositoryImpl() throws MongoDataException {
    }

    @Override
    public Actions findDefault() throws MongoDataException {
        throw new NotImplementedException();
    }

    @Override
    public Iterable<String> findActionsFor(final String securityProfile) throws MongoDataException {
        try {
          Actions secProfile = findOne(securityProfile);
            if (secProfile == null) {
                return null;
            }else{
                return secProfile.getActions().keySet();
            }
        } catch (MongoException ex) {
            log.error("Unable to ", ex);
            throw new MongoDataException("Unable to ", ex);
        }

    }
}
