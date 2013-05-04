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

import java.io.IOException;
import java.lang.reflect.Type;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class ObjectIdSerializer extends SerializerBase<ObjectId>  {

	protected ObjectIdSerializer(Class<ObjectId> t) {
		super(t);
	}
	
	public ObjectIdSerializer() {
		this(ObjectId.class);
	}

	@Override
	public void serialize(ObjectId obj, JsonGenerator jGen,	SerializerProvider sp) 
			throws IOException,	JsonProcessingException {
		jGen.writeString(obj.toString());
	}

	@Override
	public JsonNode getSchema(SerializerProvider sp, Type type)	throws JsonMappingException {
		return createSchemaNode("string");
	}
}
