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
package org.craftercms.testing.integration;

import static com.jayway.restassured.RestAssured.expect;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

public class AuditRestControllerIT {

	@Before
	public void setup() {
		RestAssured.port = 8989;
		RestAssured.basePath = "/crafter-social";
	}

	@Test
	public void getAuditByUgcIdTest() {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("Audit"))
				.given()
				.parameters("target", "test", "textContent", "Audit", "tenant",
						"test").when().post("/api/2/ugc/create.json")
				.asString();
		assertNotNull(ugc);
		JsonPath jp = new JsonPath(ugc);
		String id = jp.getString("id");
		String audit = expect().statusCode(200).given()
				.parameters("tenant", "test").when()
				.get("/api/2/audit/" + id + ".json").asString();
		jp = new JsonPath(audit);
		Map auditMap = jp.get("find {audit -> audit.ugcId =~ /" + id + "/ }");
		assertTrue(id.equals(auditMap.get("ugcId")));
	}

	@Test
	public void getAuditByUgcIdActionTest() {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("AuditAction"))
				.given()
				.parameters("target", "test", "textContent", "AuditAction",
						"tenant", "test").when().post("/api/2/ugc/create.json")
				.asString();
		assertNotNull(ugc);
		JsonPath jp = new JsonPath(ugc);
		String id = jp.getString("id");
		String audit = expect().statusCode(200).given()
				.parameters("tenant", "test").when()
				.get("/api/2/audit/" + id + "/create.json").asString();
		jp = new JsonPath(audit);
		Map auditMap = jp.get("find {audit -> audit.ugcId =~ /" + id + "/ }");
		assertTrue(id.equals(auditMap.get("ugcId")));
	}

	@Test
	public void getUGCAuditForUserTest() {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("AuditForUserActions"))
				.given()
				.parameters("target", "test", "textContent",
						"AuditForUserActions", "tenant", "test").when()
				.post("/api/2/ugc/create.json").asString();
		assertNotNull(ugc);
		JsonPath jp = new JsonPath(ugc);
		String id = jp.getString("id");
		String audit = expect().statusCode(200).given()
				.parameters("tenant", "test").when()
				.get("/api/2/audit/profile/anonymousUser.json").asString();
		jp = new JsonPath(audit);
		Map auditMap = jp.get("find {audit -> audit.ugcId =~ /" + id + "/ }");
		assertTrue(id.equals(auditMap.get("ugcId")));
	}

	@Test
	public void getUGCAuditForUserActionsTest() {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("getUGCAuditForUserActionsTest"))
				.given()
				.parameters("target", "test", "textContent",
						"getUGCAuditForUserActionsTest", "tenant", "test")
				.when().post("/api/2/ugc/create.json").asString();
		assertNotNull(ugc);
		JsonPath jp = new JsonPath(ugc);
		String id = jp.getString("id");
		String audit = expect().statusCode(200).given()
				.parameters("tenant", "test").when()
				.get("/api/2/audit/profile/anonymousUser/create.json")
				.asString();
		jp = new JsonPath(audit);
		Map auditMap = jp.get("find {audit -> audit.ugcId =~ /" + id + "/ }");
		assertTrue(id.equals(auditMap.get("ugcId")));
	}

}
