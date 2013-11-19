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
package org.craftercms.social.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.social.util.support.ResultParser;
import org.craftercms.social.util.web.Attachment;
import org.springframework.web.multipart.MultipartFile;


public interface SupportDataAccess {

	List<?> mapReduce(Class<?> entity, String mapFunction,
			String reduceFunction, ResultParser parser);

	List<?> mapReduceWithQuery(Class<?> entity, String query,
			String mapFunction, String reduceFunction, ResultParser parser);

	<T> List<T> distinct(Class<?> entity, String key, Class<T> resultClass);

	/**
	 * Saves a Object Into a GridFS
	 * 
	 * @param file
	 *            File to be Saves
	 * @return the Object Id of the file
	 * @throws IOException
	 *             If a error ocurrs while reading the file
	 */
	ObjectId saveFile(MultipartFile file) throws IOException;

	Attachment getAttachment(ObjectId attachmentId);

	void streamAttachment(ObjectId attachmentId, OutputStream output) throws Exception;

	void removeAttachment(ObjectId attachmentId);

}