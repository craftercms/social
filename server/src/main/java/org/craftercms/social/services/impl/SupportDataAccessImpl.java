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
package org.craftercms.social.services.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.social.services.SupportDataAccess;
import org.craftercms.social.util.support.ResultParser;
import org.craftercms.social.util.web.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Service
public class SupportDataAccessImpl implements SupportDataAccess {

	private final transient Logger log = LoggerFactory
			.getLogger(SupportDataAccessImpl.class);
	@Autowired
	private MongoTemplate mongoTemplate;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.craftercms.social.repositories.SupportDataAccess#saveFile(
	 * org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	public ObjectId saveFile(MultipartFile file) throws IOException {
		GridFS gFS = new GridFS(mongoTemplate.getDb());
		GridFSInputFile gFSInputFile = gFS.createFile(file.getInputStream());
		gFSInputFile.setFilename(file.getOriginalFilename());
		gFSInputFile.setContentType(file.getContentType());
		gFSInputFile.save();
		return (ObjectId) gFSInputFile.getId();
	}
	
	@Override
	public void streamAttachment(ObjectId attachmentId, OutputStream output) throws Exception {
		GridFS gFS = new GridFS(mongoTemplate.getDb());
		GridFSDBFile file = gFS.find(attachmentId);
		if (file != null) {
			file.writeTo(output);
		} else {
			log.error("Attachment with id {} does not exist " + attachmentId);
			throw new Exception("Attachment with id {} does not exist " + attachmentId);
		}
	}
	
	@Override
	public void removeAttachment(ObjectId attachmentId) {
		try {
			GridFS gFS = new GridFS(mongoTemplate.getDb());
			gFS.remove(attachmentId);
		} catch (Exception e) {
			log.error("Can not stream file.", e);
		}
	}

	@Override
	public Attachment getAttachment(ObjectId attachmentId) {
		try {
			GridFS gFS = new GridFS(mongoTemplate.getDb());
			GridFSDBFile file = gFS.find(attachmentId);
			if (file != null) {
				Attachment attach = null;
				attach = new Attachment(file.getContentType(),
							file.getLength(), file.getFilename());
				return attach;
			} else {
				log.error("Attachment with id {} does not exist", attachmentId);
				throw new DataRetrievalFailureException("Attachment with id"
						+ attachmentId.toString() + " does not exist");
			}
		} catch (Exception e) {
			log.error("Could not get attachment.", e);
			return null;
		}
	}
	
	@Override
	public <T> List<T> distinct(Class<?> entity, String key, Class<T> resultClass) {
		String inputCollectionName = entity.getSimpleName();
		Document documentAnotation = entity.getAnnotation(Document.class);
		if (documentAnotation != null) {
			inputCollectionName = documentAnotation.collection();
		}
		mongoTemplate.getDb().command(
				String.format("{ distinct : '%s', key : '%s' }",
						inputCollectionName, key));

		return null;
	}


	@Override
	public List<?> mapReduce(Class<?> entity, String mapFunction,
			String reduceFunction, ResultParser parser) {
		String inputCollectionName = entity.getSimpleName();
		Document documentAnotation = entity.getAnnotation(Document.class);
		if (documentAnotation != null) {
			inputCollectionName = documentAnotation.collection();
		}
		MapReduceResults<?> r = mongoTemplate.mapReduce(inputCollectionName,
				mapFunction, reduceFunction, entity);
		return parser.parseList(r.getRawResults().toMap());
	}
	

	@Override
	public List<?> mapReduceWithQuery(Class<?> entity, String query,
			String mapFunction, String reduceFunction, ResultParser parser) {
		String inputCollectionName = entity.getSimpleName();
		Document documentAnotation = entity.getAnnotation(Document.class);
		if (documentAnotation != null) {
			inputCollectionName = documentAnotation.collection();
		}
		MapReduceResults<?> r = mongoTemplate.mapReduce(new BasicQuery(query),inputCollectionName,
				mapFunction, reduceFunction, entity);
		return parser.parseList(r.getRawResults().toMap());
	}

}
