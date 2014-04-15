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

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.exceptions.AuditException;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.craftercms.social.services.AuditServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditServicesImpl implements AuditServices {
    private Logger log = LoggerFactory.getLogger(AuditServicesImpl.class);
	@Autowired
	private UGCAuditRepository uGCAuditRepository;
	
	@Override
	public Iterable<UGCAudit> findUGCAudits(ObjectId ugcID) throws AuditException {
        try {
            return uGCAuditRepository.findByUgcId(ugcID);
        } catch (MongoDataException e) {
            log.error("Unable to find Audits for ugc"+ugcID, e);
            throw new AuditException("Unable to find Audits for ugc",e);
        }
    }

	@Override
	public Iterable<UGCAudit> findUGCAudits(ObjectId ugcId, AuditAction auditAction) throws AuditException {
        try {
            return uGCAuditRepository.findByUgcIdAndAction(ugcId, auditAction);
        }catch (MongoDataException ex){
            log.error("Unable to find ugcs by id "+ugcId+" and auditAction "+auditAction, ex);
            throw new AuditException("Unable to find Audits for ugc and action",ex);
        }
	}

	@Override
	public UGCAudit findUGCAudits(ObjectId ugcId, AuditAction action,
			String profileId) throws AuditException {
        try {
            return uGCAuditRepository.findByProfileIdAndUgcIdAndAction(profileId, ugcId, action);
        } catch (MongoDataException e) {
            log.error("Unable to find ugcs by id " + ugcId + " and auditAction " + action, e);
            throw new AuditException("Unable to find Audits for ugc and action",e);
        }
    }

	@Override
	public Iterable<UGCAudit> findUGCAudits(ObjectId ugcId, String profileId) throws AuditException {
        try {
            return uGCAuditRepository.findByUgcIdAndProfileId(ugcId,profileId);
        } catch (MongoDataException e) {
            log.error("Unable to find ugcs by id " + ugcId + " and profileid " + profileId, e);
            throw new AuditException("Unable to find Audits for ugc and action",e);
        }
    }

	@Override
	public Iterable<UGCAudit> findUGCAuditsForProfile(String profileId, AuditAction auditAction) throws AuditException {
        try {
            return uGCAuditRepository.findByProfileIdAndAction(profileId,auditAction);
        } catch (MongoDataException e) {
            log.error("Unable to find ugcs by profileid" +profileId + " and auditAction " + auditAction, e);
            throw new AuditException("Unable to find Audits for ugc and action",e);
        }

    }

	@Override
	public Iterable<UGCAudit> findUGCAuditsForProfile(String profileId) throws AuditException {
        try {
            return uGCAuditRepository.findByProfileId(profileId);
        } catch (MongoDataException e) {
            log.error("Unable to find ugcs by profileID"+profileId, e);
            throw new AuditException("Unable to find Audits for ugc and action",e);
        }
    }

	
}
