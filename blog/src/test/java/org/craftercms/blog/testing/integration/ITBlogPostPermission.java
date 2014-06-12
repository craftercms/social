package org.craftercms.blog.testing.integration;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.craftercms.blog.testing.IntegrationTestingBase;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

public class ITBlogPostPermission extends IntegrationTestingBase {
	
	private String childUgcId;
	
	@Before
	public void prepareDriver() throws Exception {
		super.prepareDriver();
		childUgcId = this.myNewCommentPost(this.ticket, "craftercms");
	}
	
	@Test
	public void testAnonymousCantLike() {
		System.out.println(" Integration Test testAnonymousCantLike  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		boolean found = false;
		try {
			WebElement reply = driver.findElement(By.className("like"));
			String classExpected = "notAllowed";
			String classes = reply.getAttribute("class");
			if (classes !=null && !classes.isEmpty()) {
				String[] classArray = classes.split(" ");
				for (String current: classArray) {
					if (current.equalsIgnoreCase(classExpected)) {
						found =true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(" testAnonymousCantLike textContentField ERROR 2 " + e.getMessage());
		}
		assertEquals(true, found);
	}
	
	
	@Test
	public void testAnonymousCantFlag() {
		System.out.println(" Integration Test testAnonymousCantFlag  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		boolean found = false;
		try {
			WebElement reply = driver.findElement(By.className("flag"));
			String classExpected = "notAllowed";
			String classes = reply.getAttribute("class");
			if (classes !=null && !classes.isEmpty()) {
				String[] classArray = classes.split(" ");
				for (String current: classArray) {
					if (current.equalsIgnoreCase(classExpected)) {
						found =true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(" testAnonymousCantFlag textContentField ERROR 2 " + e.getMessage());
		}
		assertEquals(true, found);
	}
	
	
	@Test
	public void testAnonymousCantReply() {
		System.out.println(" Integration Test testAnonymousCantReply  1 " + currentUGCId);
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		boolean found = false;
		try {
			WebElement reply = driver.findElement(By.className("reply"));
			String classExpected = "notAllowed";
			String classes = reply.getAttribute("class");
			if (classes !=null && !classes.isEmpty()) {
				String[] classArray = classes.split(" ");
				for (String current: classArray) {
					if (current.equalsIgnoreCase(classExpected)) {
						found =true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(" testAnonymousCantReply textContentField ERROR 2 " + e.getMessage());
		}
		assertEquals(true, found);
	}
	
	@Test
	public void testAnonymousCantComment() {
		System.out.println(" Integration Test testAnonymousCantReply  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		boolean found = false;
		try {
			WebElement webUgcEditor = driver.findElement(By.className("ugc-editor"));
			String classExpected = "notAllowed";
			String classes = webUgcEditor.getAttribute("class");
			if (classes !=null && !classes.isEmpty()) {
				String[] classArray = classes.split(" ");
				for (String current: classArray) {
					if (current.equalsIgnoreCase(classExpected)) {
						found =true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(" testAnonymousCantComment textContentField ERROR 2 " + e.getMessage());
		}
		assertEquals(true, found);
	}
	
	private String myNewCommentPost(String ticket, String tenant) {
		RestAssured.basePath = "/crafter-comments";
		String newContent = "Hello World!";
		String ugc = expect()
				.statusCode(201)
				.given()
				.parameters("ticket", ticket, 
						"target", tenant, 
						"textContent", newContent,
						"parentId",this.currentUGCId,
						"tenant", tenant).
						when().
						post("/api/2/ugc/create.json")
				.asString();
		JsonPath jp = new JsonPath(ugc);
		String childUgcId = jp.getString("id");
		return childUgcId;
	}
	
	protected String createNewPost(String ticket, String tenant) {
		System.out.println(" Integration Test createNewPost child  ");
		RestAssured.basePath = "/crafter-comments";
		
		String newContent = "{\"title\":\"NEW_ENTRY\",\"image\":\"\",\"content\":\"<p>Hello World!</p>\"}";
		String ugc = expect()
				.statusCode(201)
				.given()
				.parameters("ticket", ticket, 
						"target", tenant, 
						"textContent", newContent,
						"tenant", tenant).
						when().
						post("/api/2/ugc/create.json")
				.asString();
		
		JsonPath jp = new JsonPath(ugc);
		currentUGCId = jp.getString("id");
		
		
		newContent = "Testing comment";
		ugc = expect()
				.statusCode(201)
				.given()
				.parameters("ticket", ticket, 
						"target", tenant, 
						"textContent", newContent,
						"parentId",currentUGCId,
						"tenant", tenant).
						when().
						post("/api/2/ugc/create.json")
				.asString();
		
		return currentUGCId;
	}

}
