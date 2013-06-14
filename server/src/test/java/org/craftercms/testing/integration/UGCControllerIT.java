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
import org.junit.Test;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import static org.hamcrest.Matchers.equalTo;

public class UGCControllerIT {

	@Before
	public void setup() {
		RestAssured.port = 8989;
		RestAssured.basePath = "/crafter-social";
	}

	@Test
	public void testCreateUgc() throws Exception {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("CONTENT"))
				.given()
				.parameters("target", "test", "textContent", "CONTENT",
						"tenant", "test").when().post("/api/2/ugc/create.json")
				.asString();
		JsonPath jp = new JsonPath(ugc);
		String id = jp.getString("id");
		assertNotNull(id);
	}

	@Test
	public void testUpdateUgc() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("CONTENT"))
				.given()
				.parameters("target", "test", "textContent", "CONTENT",
						"tenant", "test").when().post("/api/2/ugc/create.json");
		String targets = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/target.json").asString();
		JsonPath jp = new JsonPath(targets);
		jp.setRoot("list");
		Map ugcMap = jp.get("find {ugc -> ugc.textContent =~ /CONTENT/ }");
		String id = (String) ugcMap.get("id");
		expect().statusCode(200)
				.body("textContent", equalTo("CONTENT Update"))
				.given()
				.parameters("ugcId", id, "target", "test", "textContent",
						"CONTENT Update", "tenant", "test").when()
				.post("/api/2/ugc/update.json");
	}

	@Test
	public void testDeleteUgc() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("MY CONTENT"))
				.given()
				.parameters("target", "test", "textContent", "MY CONTENT",
						"tenant", "test").when().post("/api/2/ugc/create.json");
		String targets = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/target.json").asString();
		JsonPath jp = new JsonPath(targets);
		jp.setRoot("list");
		Map ugcMap = jp.get("find {ugc -> ugc.textContent =~ /MY CONTENT/ }");
		String id = (String) ugcMap.get("id");
		expect().statusCode(200).given().parameters("tenant", "test").when()
				.post("/api/2/ugc/delete/" + id + ".json");
	}

	@Test
	public void testDislikeUgc() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("DISLIKE CONTENT"))
				.given()
				.parameters("target", "test", "textContent", "DISLIKE CONTENT",
						"tenant", "test").when().post("/api/2/ugc/create.json");
		String targets = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/target.json").asString();
		JsonPath jp = new JsonPath(targets);
		jp.setRoot("list");
		Map ugcMap = jp
				.get("find {ugc -> ugc.textContent =~ /DISLIKE CONTENT/ }");
		String id = (String) ugcMap.get("id");
		expect().statusCode(200).given().parameters("tenant", "test").when()
				.post("/api/2/ugc/dislike/" + id + ".json");
		targets = given().parameters("target", "test", "tenant", "test").when()
				.get("/api/2/ugc/target.json").asString();
		jp = new JsonPath(targets);
		jp.setRoot("list");
		ugcMap = jp.get("find {ugc -> ugc.textContent =~ /DISLIKE CONTENT/ }");
		assertTrue(((Integer) ugcMap.get("offenceCount")) > 0);
	}

	@Test
	public void testLikeUgc() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("LIKE CONTENT"))
				.given()
				.parameters("target", "test", "textContent", "LIKE CONTENT",
						"tenant", "test").when().post("/api/2/ugc/create.json");
		String targets = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/target.json").asString();
		JsonPath jp = new JsonPath(targets);
		jp.setRoot("list");
		Map ugcMap = jp.get("find {ugc -> ugc.textContent =~ /LIKE CONTENT/ }");
		String id = (String) ugcMap.get("id");
		expect().statusCode(200).given().parameters("tenant", "test").when()
				.post("/api/2/ugc/like/" + id + ".json");
		targets = given().parameters("target", "test", "tenant", "test").when()
				.get("/api/2/ugc/target.json").asString();
		jp = new JsonPath(targets);
		jp.setRoot("list");
		ugcMap = jp.get("find {ugc -> ugc.textContent =~ /LIKE CONTENT/ }");
		assertTrue(((Integer) ugcMap.get("likeCount")) > 0);
	}

	@Test
	public void testFlagUgc() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("FLAG CONTENT"))
				.given()
				.parameters("target", "test", "textContent", "FLAG CONTENT",
						"tenant", "test").when().post("/api/2/ugc/create.json");
		String targets = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/target.json").asString();
		JsonPath jp = new JsonPath(targets);
		jp.setRoot("list");
		Map ugcMap = jp.get("find {ugc -> ugc.textContent =~ /FLAG CONTENT/ }");
		String id = (String) ugcMap.get("id");
		expect().statusCode(200).given()
				.parameters("tenant", "test", "reason", "test").when()
				.post("/api/2/ugc/flag/" + id + ".json");
		targets = given()
				.parameters("target", "test", "target", "test", "tenant",
						"test").when().get("/api/2/ugc/target.json").asString();
		jp = new JsonPath(targets);
		jp.setRoot("list");
		ugcMap = jp.get("find {ugc -> ugc.textContent =~ /FLAG CONTENT/ }");
		assertTrue(((Integer) ugcMap.get("flagCount")) > 0);
	}

	@Test
	public void testFindByUGCId() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("FIND BY ID"))
				.given()
				.parameters("target", "test", "textContent", "FIND BY ID",
						"tenant", "test").when().post("/api/2/ugc/create.json");
		String targets = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/target.json").asString();
		JsonPath jp = new JsonPath(targets);
		jp.setRoot("list");
		Map ugcMap = jp.get("find {ugc -> ugc.textContent =~ /FIND BY ID/ }");
		String id = (String) ugcMap.get("id");
		String ugc = given()
				.parameters("target", "test", "target", "test", "tenant",
						"test").when()
				.get("/api/2/ugc/get_ugc/" + id + ".json").asString();
		jp = new JsonPath(ugc);
		assertTrue(jp.getString("textContent").equalsIgnoreCase("FIND BY ID"));
	}

	@Test
	public void testModerationStatus() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("Moderation Status"))
				.given()
				.parameters("target", "test", "textContent",
						"Moderation Status", "tenant", "test").when()
				.post("/api/2/ugc/create.json");
		String ugcList = given().parameters("tenant", "test").when()
				.get("/api/2/ugc/moderation/UNMODERATED.json").asString();
		JsonPath jp = new JsonPath(ugcList);
		Map ugcMap = jp
				.get("find {ugc -> ugc.textContent =~ /Moderation Status/ }");
		String status = (String) ugcMap.get("moderationStatus");
		assertTrue(status.equals("UNMODERATED"));
	}

	@Test
	public void testModerationStatusTarget() throws Exception {
		expect().statusCode(201)
				.body("textContent", equalTo("Moderation Status & Target"))
				.given()
				.parameters("target", "test", "textContent",
						"Moderation Status & Target", "tenant", "test").when()
				.post("/api/2/ugc/create.json");
		String ugcList = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/moderation/UNMODERATED/target.json")
				.asString();
		JsonPath jp = new JsonPath(ugcList);
		Map ugcMap = jp
				.get("find {ugc -> ugc.textContent =~ /Moderation Status & Target/ }");
		String status = (String) ugcMap.get("moderationStatus");
		assertTrue(status.equals("UNMODERATED"));
	}

	@Test
	public void testUpdateModerationStatus() throws Exception {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("Update Moderation Status"))
				.given()
				.parameters("target", "test", "textContent",
						"Update Moderation Status", "tenant", "test").when()
				.post("/api/2/ugc/create.json").asString();
		JsonPath jp = new JsonPath(ugc);
		String status = jp.getString("moderationStatus");
		assertTrue(status.equals("UNMODERATED"));
		String id = jp.getString("id");
		String ugcUpdated = given()
				.parameters("tenant", "test", "moderationStatus", "APPROVED")
				.when().post("/api/2/ugc/moderation/" + id + "/status.json")
				.asString();
		jp = new JsonPath(ugcUpdated);
		status = jp.getString("moderationStatus");
		assertTrue(status.equals("APPROVED"));
	}

	@Test
	public void testCountTenantTarget() throws Exception {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("Count"))
				.given()
				.parameters("target", "test", "textContent", "Count", "tenant",
						"test").when().post("/api/2/ugc/create.json")
				.asString();
		assertNotNull(ugc);
		String count = expect().statusCode(200).given()
				.parameters("target", "test", "tenant", "test").when()
				.get("/api/2/ugc/count.json").asString();
		assertTrue(Integer.parseInt(count) > 0);
	}

	@Test
	public void testGetTarget() throws Exception {
		String ugc = expect()
				.statusCode(201)
				.body("textContent", equalTo("Target"))
				.given()
				.parameters("target", "test", "textContent", "Target",
						"tenant", "test").when().post("/api/2/ugc/create.json")
				.asString();
		assertNotNull(ugc);
		String targets = given().parameters("target", "test", "tenant", "test")
				.when().get("/api/2/ugc/target.json").asString();
		JsonPath jp = new JsonPath(targets);
		jp.setRoot("list");
		Map ugcMap = jp.get("find {ugc -> ugc.textContent =~ /Target/ }");
		assertNotNull(ugcMap.get("id"));
	}

}
