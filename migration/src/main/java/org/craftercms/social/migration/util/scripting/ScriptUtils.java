/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.migration.util.scripting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Map;

import org.craftercms.commons.crypto.CipherUtils;
import org.craftercms.commons.crypto.CryptoUtils;
import org.jongo.FindAndModify;
import org.mozilla.javascript.NativeObject;

/**
 * Created by Carlos Ortiz on 11/20/14.
 */
public class ScriptUtils {

    public static String toJson(Object o) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.getSerializationConfig().without(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.getSerializationConfig().with(SerializationFeature.WRITE_NULL_MAP_VALUES,SerializationFeature
            .WRITE_EMPTY_JSON_ARRAYS);
        return mapper.writeValueAsString(o);
    }


    public static NativeObject toJSObject(Map map){
        final NativeObject toReturn = new NativeObject();
        for (Object key : map.keySet()) {
            toReturn.defineProperty(key.toString(),map.get(key),NativeObject.READONLY);
        }
        return toReturn;
    }


    public static String hash(final String clearString){
     return CipherUtils.hashPassword(clearString);
    }

    public static FindAndModify update(final FindAndModify findAndModify,final String query,final Object... params){
        return findAndModify.with(query,params);
    }
}
