package org.craftercms.social.util.support.spring.data;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: alfonsovasquez
 * Date: 27/11/13
 * Time: 09:13
 * To change this template use File | Settings | File Templates.
 */
public class AttributesConverter implements Converter<DBObject, Map<String, Serializable>> {

    @Override
    public Map<String, Serializable> convert(DBObject source) {
        return source.toMap();
    }

}
