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

import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.social.domain.UGCAudit;
import org.craftercms.social.domain.UGCAudit.AuditAction;
import org.craftercms.social.repositories.UGCAuditRepository;
import org.craftercms.social.services.AuditServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditServicesImpl implements AuditServices {

	@Autowired
	private UGCAuditRepository repository;
	
	@Override
	public List<UGCAudit> findUGCAudits(ObjectId ugcID) {
		return repository.findByUgcId(ugcID);
	}

	@Override
	public List<UGCAudit> findUGCAudits(ObjectId ugcId, AuditAction auditAction) {
		return repository.findByUgcIdAndAction(ugcId,auditAction);
	}

	@Override
	public UGCAudit findUGCAudits(ObjectId ugcId, AuditAction action,
			String profileId) {
		return repository.findByProfileIdAndUgcIdAndAction(profileId, ugcId, action);
	}

	@Override
	public List<UGCAudit> findUGCAudits(ObjectId ugcId, String profileId) {
		return repository.findByUgcIdAndProfileId(ugcId,profileId);
	}

	@Override
	public List<UGCAudit> findUGCAuditsForProfile(String profileId,
			AuditAction auditAction) {
		return repository.findByProfileIdAndAction(profileId,auditAction);
		
	}

	@Override
	public List<UGCAudit> findUGCAuditsForProfile(String profileId) {
		return repository.findByProfileId(profileId);
	}

	
}
