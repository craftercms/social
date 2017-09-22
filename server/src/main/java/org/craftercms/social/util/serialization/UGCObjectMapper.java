/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.util.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"rawtypes", "unchecked"})
public class UGCObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1669827811287238948L;
    private List<JsonSerializer> serializerList = new ArrayList();
    private Map<Class, JsonDeserializer> deserializerMap = new HashMap<>();


    public UGCObjectMapper(List<JsonSerializer> serializerList, Map<Class, JsonDeserializer> deserializerMap) {
        super();
        super.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        super.getSerializationConfig().without(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        super.getSerializationConfig().with(SerializationFeature.WRITE_NULL_MAP_VALUES);

        this.serializerList = serializerList;
        this.deserializerMap = deserializerMap;
        registerSerializationModule();
    }

    protected void registerSerializationModule() {
        SimpleModule module = new SimpleModule("UGCSerializationModule", new Version(1, 0, 0, null, null, null));

        for (JsonSerializer ser : serializerList) {
            module.addSerializer(ser);
        }

        for (Class key : deserializerMap.keySet()) {
            JsonDeserializer deser = deserializerMap.get(key);
            module.addDeserializer(key, deser);
        }

        registerModule(module);

    }

    public List<JsonSerializer> getSerializerList() {
        return serializerList;
    }

    public void setSerializerList(List<JsonSerializer> serializerList) {
        this.serializerList = serializerList;
    }

    public Map<Class, JsonDeserializer> getDeserializerMap() {
        return deserializerMap;
    }

    public void setDeserializerMap(Map<Class, JsonDeserializer> deserializerMap) {
        this.deserializerMap = deserializerMap;
    }
}
