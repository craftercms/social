package org.craftercms.social.repositories;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.MongoException;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Counter;
import org.craftercms.social.exceptions.CounterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class CounterRepositoryImpl extends JongoRepository<Counter> implements CounterRepository{

	private Logger log = LoggerFactory.getLogger(CounterRepositoryImpl.class);
	private static final long INIT_VALUE = 1l;
	private static final String SEQUENCE_FIELD = "seq";

    /**
     * Creates a instance of a Jongo Repository.
     */
    public CounterRepositoryImpl() throws MongoDataException {
    }

    public long getNextSequence(String collectionName) throws CounterException {
        log.debug("Setting next Sequence for collection {}",collectionName);
        String increment = getQueryFor("social.counter.incrementSequence");
        String find = getQueryFor("social.counter.byId");
        try {
            Counter counter = getCollection().findAndModify(find,collectionName).with(increment,1).as(Counter.class);
            if (counter == null) {
                counter = new Counter();
                counter.setId(collectionName);
                counter.setSeq(INIT_VALUE);
                save(counter);
            }
            return counter.getSeq();
        }catch (MongoDataException | MongoException ex){
            log.error("Unable set next sequence for collection "+collectionName, ex);
            throw new CounterException("Unable to Generate next Sequence",ex);
        }
	} 

}
