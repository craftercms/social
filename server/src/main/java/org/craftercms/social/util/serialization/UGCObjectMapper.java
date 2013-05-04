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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UGCObjectMapper extends ObjectMapper {
	private List<JsonSerializer> serializerList = new ArrayList();
	private Map<Class, JsonDeserializer> deserializerMap = new HashMap<Class, JsonDeserializer>();   
	
	
	public UGCObjectMapper(List<JsonSerializer> serializerList, Map<Class, JsonDeserializer> deserializerMap) {
		super();
		super.setSerializationInclusion(Inclusion.NON_NULL);
		super.getSerializationConfig().without(Feature.FAIL_ON_EMPTY_BEANS);	
		super.getSerializationConfig().with(Feature.WRITE_NULL_MAP_VALUES, Feature.WRITE_EMPTY_JSON_ARRAYS);
		this.serializerList = serializerList;
		this.deserializerMap = deserializerMap;
		registerSerializationModule();
	}
	
	protected void registerSerializationModule() {
		SimpleModule module = new SimpleModule("UGCSerializationModule", new Version(1, 0, 0, null));

		for (JsonSerializer ser:serializerList) {
			module.addSerializer(ser);
		}
			
		for (Class key:deserializerMap.keySet()) {
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
