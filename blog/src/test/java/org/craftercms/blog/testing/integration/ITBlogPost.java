package org.craftercms.blog.testing.integration;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.craftercms.blog.testing.IntegrationTestingBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

public class ITBlogPost extends IntegrationTestingBase{
	
	@Test
	public void testReplyComment() {

		System.out.println("Integration Test testReplyComment  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login")).click();
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" testReplyComment WAITING ERROR");
		}
		try {
			(new WebDriverWait(driver, 30)).until(ExpectedConditions
					.presenceOfElementLocated(By.id("textContentField")));
		} catch(Exception e) {}
		driver.findElement(By.id("textContentField")).sendKeys("HELLO WORLD!");
		driver.findElement(By.id("post-comment")).click();
		try {
			(new WebDriverWait(driver, 30)).until(ExpectedConditions
					.presenceOfElementLocated(By.className("reply")));
		} catch(Exception e) {}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" testReplyComment WAITING ERROR");
		}
		driver.findElement(By.className("reply")).click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.id("reply-comment")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" testReplyComment WAITING ERROR");
		}
		JavascriptExecutor exec = (JavascriptExecutor) driver;
		try {
			exec.executeScript("document.getElementById('reply-comment').value = 'REPLY MESSAGE'");
		} catch (Exception e) {
			System.out.println(" testReplyComment EROR " + e.getMessage());
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println(" testReplyComment WAITING ERROR");
		}
		driver.findElement(By.id("post-reply-btn")).click();
		try {
			Thread.sleep(500);

		} catch (InterruptedException e) {
			System.out.println("testReplyComment  WAITING ERROR");
		}

//		List<WebElement> el = driver.findElements(By.className("commenting"));
//		boolean found = false;
//		for (WebElement c : el) {
//			if (c.getText().equals("REPLY MESSAGE")) {
//				found = true;
//				break;
//			}
//		}
//		assertTrue(found);
	}
	
	@Test
	public void testSimpleComment() {

		System.out.println(" Integration Test testSimpleComment  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login")).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		driver.findElement(By.id("textContentField")).sendKeys("HELLO WORLD!");
		driver.findElement(By.id("post-comment")).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		List<WebElement> el = driver.findElements(By.className("commenting"));
		boolean found = false;
		for (WebElement c : el) {
			if (c.getText().equals("HELLO WORLD!")) {
				found = true;
				break;
			}
		}
		assertTrue(found);

	}
	
	@Test
	public void testLike() {
		System.out.println(" Integration Test testLike  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login")).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		WebElement we = driver.findElement(By.className("like-" + this.currentUGCId));
		//WebElement current = null;
		if (we != null) {
			we.click();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		we = driver.findElement(By.className("like-" + this.currentUGCId));
		String likes = null;
		if (we != null) {
			likes = we.getAttribute("innerHTML");
		}
		assertTrue(likes != null && Integer.parseInt(likes) == 1);

	}
	
	@Test
	public void testFlag() {
		System.out.println(" Integration Test testFlag  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login")).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		WebElement we = driver.findElement(By.className("flag-" + this.currentUGCId));
		//WebElement current = null;
		if (we != null) {
			we.click();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		we = driver.findElement(By.className("flag-" + this.currentUGCId));
		String flags = null;
		if (we != null) {
			flags = we.getAttribute("innerHTML");
		}
		assertTrue(flags != null && Integer.parseInt(flags) == 1);
	}

	protected String createNewPost(String ticket, String tenant) {
		System.out.println(" Integration Test createNewPost child  " + ticket +" tenant "+ tenant);
		RestAssured.basePath = "/crafter-social";
		
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