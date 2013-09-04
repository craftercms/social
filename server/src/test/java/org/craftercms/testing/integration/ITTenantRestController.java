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

import static com.jayway.restassured.RestAssured.*;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

public class ITTenantRestController {

	@Before
	public void setup() {
		RestAssured.port = 8989;
		RestAssured.basePath = "/crafter-social";
	}

	@Test
	public void setTenantTest() {
		String tenant = expect().given()
				.parameters("roles", "SOCIAL_USER", "tenant", "testingtenant")
				.when().post("/api/2/tenant/set_tenant.json").asString();
		JsonPath jp = new JsonPath(tenant);
		String id = jp.getString("id");
		assertNotNull(id);
	}

	@Test
	public void setTenantRolesTest() {
		String tenant = expect()
				.given()
				.parameters("roles", "SOCIAL_USER", "tenant",
						"testingtenantroles").when()
				.post("/api/2/tenant/set_tenant.json").asString();
		JsonPath jp = new JsonPath(tenant);
		String id = jp.getString("id");
		assertNotNull(id);
		expect().statusCode(200)
				.given()
				.parameters("roles", "SOCIAL_ADMIN", "tenant",
						"testingtenantroles").when()
				.post("/api/2/tenant/set_tenant_roles.json");
		tenant = expect().statusCode(200).given()
				.parameters("tenant", "testingtenantroles").when()
				.get("/api/2/tenant/get_tenant.json").asString();
		jp = new JsonPath(tenant);
		String role = jp.getString("roles[0]");
		assertNotNull(role);
		assertTrue(role.equalsIgnoreCase("SOCIAL_ADMIN"));
	}

	@Test
	public void getTenantTest() {
		String tenant = expect().given()
				.parameters("roles", "USER", "tenant", "gettenant").when()
				.post("/api/2/tenant/set_tenant.json").asString();
		JsonPath jp = new JsonPath(tenant);
		String id = jp.getString("id");
		assertNotNull(id);
		tenant = expect().statusCode(200).given()
				.parameters("tenant", "gettenant").when()
				.get("/api/2/tenant/get_tenant.json").asString();
		jp = new JsonPath(tenant);
		String tenantName = jp.getString("tenantName");
		assertNotNull(tenantName);
		assertTrue(tenantName.equalsIgnoreCase("gettenant"));
	}

	@Test
	public void getDeleteTenantTest() {
		String tenant = expect().given()
				.parameters("roles", "DELETE_USER", "tenant", "deletetenant")
				.when().post("/api/2/tenant/set_tenant.json").asString();
		JsonPath jp = new JsonPath(tenant);
		String id = jp.getString("id");
		assertNotNull(id);
		expect().statusCode(200).given().parameters("tenant", "deletetenant")
				.when().post("/api/2/tenant/delete_tenant.json");
		tenant = expect().statusCode(200).given()
				.parameters("tenant", "deletetenant").when()
				.get("/api/2/tenant/get_tenant.json").asString();
		assertTrue(tenant == null || tenant.equals(""));
	}
}
