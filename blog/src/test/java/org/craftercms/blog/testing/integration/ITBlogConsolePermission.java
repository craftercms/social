package org.craftercms.blog.testing.integration;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.craftercms.blog.testing.IntegrationTestingBase;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

public class ITBlogConsolePermission extends IntegrationTestingBase {

	private String childUgcId;

	@Test
	public void testUpdateAuthorEntryWithAuthor() {
		String authorUgcId = createNewPost("author", "author", "craftercms");

		System.out
				.println(" Integration Test testUpdateAuthorEntryWithAuthor  ");
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
		driver.findElement(By.id("username")).sendKeys("author");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("author");
		driver.findElement(By.id("login")).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		WebElement t = driver.findElement(By.id("blogconsole"));
		t.click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("#mytable")));
		// CHANGED
		driver.findElement(By.name(authorUgcId)).click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By
						.cssSelector("#textContentField_ifr")));

		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By
						.cssSelector("#textContentField_ifr")));
		driver.findElement(By.className("editEntry")).click();
		JavascriptExecutor exec = (JavascriptExecutor) driver;
		try {
			exec.executeScript("var _tmp = tinymce.get('textContentField'); _tmp.setContent('Hello World!'); _tmp.save();");
		} catch (Exception e) {
			System.out.println(" EROR " + e.getMessage());
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		try {
			new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return !d.findElement(By.cssSelector("#textContentField"))
							.getAttribute("value").isEmpty();
				}
			});
		} catch (Exception e) {}
		String newTitle = "Updated";
		driver.findElement(By.id("titleEntry")).clear();
		driver.findElement(By.id("titleEntry")).sendKeys(newTitle);
		assertEquals(newTitle, driver.findElement(By.id("titleEntry"))
				.getAttribute("value"));
		driver.findElement(By.className("updateEntry")).click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("#viewBlog")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		String text = driver.findElement(By.name(authorUgcId))
				.getAttribute("innerHTML");
		assertEquals(newTitle, text);
	}

	@Test
	public void testRegularUserCantAccessConsole() {
		System.out
				.println(" Integration Test testRegularUserCantAccessConsole  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("regular");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("regular");
		driver.findElement(By.id("login")).click();
		WebElement t = driver.findElement(By.id("blogconsole"));
		assertNotNull(t);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}

		String display = t.getCssValue("display");
		boolean isNotDisplay = false;
		if (display != null && !display.isEmpty()) {
			isNotDisplay = display.equalsIgnoreCase("none");
		}
		assertTrue(isNotDisplay);
	}

	@Test
	public void testUpdateAdminEntryWithRegular() {
		System.out
				.println(" Integration Test testUpdateAdminEntryWithRegular  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("author");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("author");
		driver.findElement(By.id("login")).click();
		WebElement t = driver.findElement(By.id("blogconsole"));
		t.click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("#mytable")));
		// CHANGED
		driver.findElement(By.name(this.currentUGCId)).click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By
						.cssSelector("#textContentField_ifr")));

		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.className("edit")));

		WebElement edit = driver.findElement(By.className("edit"));
		String display = edit.getCssValue("display");
		boolean isNotDisplay = false;
		if (display != null && !display.isEmpty()) {
			isNotDisplay = display.equalsIgnoreCase("none");
		}
		assertTrue(isNotDisplay);
		WebElement update = driver.findElement(By.className("update"));
		display = update.getCssValue("display");
		isNotDisplay = false;
		if (display != null && !display.isEmpty()) {
			isNotDisplay = display.equalsIgnoreCase("none");
		}
		assertTrue(isNotDisplay);
	}

	@Test
	public void testDeleteAdminEntryWithRegular() {
		System.out
				.println(" Integration Test testDeleteAdminEntryWithRegular  ");
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
		driver.findElement(By.id("username")).sendKeys("author");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("author");
		driver.findElement(By.id("login")).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		WebElement t = driver.findElement(By.id("blogconsole"));
		t.click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("#mytable")));
		// CHANGED
		driver.findElement(By.name(this.currentUGCId)).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By
						.cssSelector("#textContentField_ifr")));

		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.className("edit")));

		WebElement delete = driver.findElement(By.className("delete"));

		String display = delete.getCssValue("display");
		boolean isNotDisplay = false;
		if (display != null && !display.isEmpty()) {
			isNotDisplay = display.equalsIgnoreCase("none");
		}
		assertTrue(isNotDisplay);
	}

	private String createNewPost(String username, String password, String tenant) {
		String appToken = null;
		String ticket = null;
		String childUgcId = null;
		try {
			appToken = client.getAppToken("craftersocial", "craftersocial");
			ticket = client.getTicket(appToken, username, password,
					"craftercms");

			RestAssured.basePath = "/crafter-social";
			String newContent = "{\"title\":\"NEW_ENTRY_AUTHOR\",\"image\":\"\",\"content\":\"<p>Hello World AUTHOR!</p>\"}";
			String ugc = expect()
					.statusCode(201)
					.given()
					.parameters("ticket", ticket, 
							"target", tenant,
							"textContent", newContent, 
							"tenant", tenant).when()
					.post("/api/2/ugc/create.json").asString();
			JsonPath jp = new JsonPath(ugc);
			childUgcId = jp.getString("id");
		} catch (Exception e) {
			System.out.println(" createNewPost error: " + e.getMessage());
		}
		return childUgcId;
	}

}
