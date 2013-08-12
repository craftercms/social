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

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.RestAssured.expect;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import static org.hamcrest.Matchers.equalTo;

public class ITPermissionRestController {

	@Before
	public void init() {
		RestAssured.port = 8989;
		RestAssured.basePath = "/crafter-social";
	}

	@Test
	public void testAdminPermission() throws Exception {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("Permission Create"))
				.given()
				.parameters("target", "test", "textContent",
						"Permission Create", "action_read", "SOCIAL_USER",
						"action_create", "SOCIAL_ADMIN", "action_update",
						"SOCIAL_ADMIN", "action_delete", "SOCIAL_ADMIN",
						"tenant", "test").when().post("/api/2/ugc/create.json")
				.asString();
		JsonPath jp = new JsonPath(ugc);
		String id = jp.getString("id");
		assertNotNull(id);
		String permission = given()
				.parameters("target", "test", "tenant", "test").when()
				.get("/api/2/permission/" + id + "/read.json").asString();
		assertTrue(!Boolean.parseBoolean(permission));
		permission = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/permission/" + id + "/create.json")
				.asString();
		assertTrue(Boolean.parseBoolean(permission));
		permission = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/permission/" + id + "/update.json")
				.asString();
		assertTrue(Boolean.parseBoolean(permission));
		permission = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/permission/" + id + "/delete.json")
				.asString();
		assertTrue(Boolean.parseBoolean(permission));
	}

	@Test
	public void testCreateAction() throws Exception {
		String tenantInfo = expect().given()
				.parameters("tenant", "mynewtenant").when()
				.get("/api/2/tenant/get_tenant.json").asString();
		String tenant = expect().given()
				.parameters("roles", "SOCIAL_USER", "tenant", "mynewtenant")
				.when().post("/api/2/tenant/set_tenant.json").asString();
		String createPermision = given().parameters("tenant", "mynewtenant")
				.when().get("/api/2/permission/create.json").asString();
		assertTrue(!Boolean.parseBoolean(createPermision));
	}

}
