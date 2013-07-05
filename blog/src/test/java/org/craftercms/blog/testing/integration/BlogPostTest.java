package org.craftercms.blog.testing.integration;

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

public class BlogPostTest extends IntegrationTestingBase{
	
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
		driver.findElement(By.id("textContentField")).sendKeys("HELLO WORLD!");
		driver.findElement(By.id("post-comment")).click();
		(new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(By.className("reply")));
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
		driver.findElement(By.id("post-reply-btn")).click();
		try {
			Thread.sleep(1000);

		} catch (InterruptedException e) {
			System.out.println("testReplyComment  WAITING ERROR");
		}

		List<WebElement> el = driver.findElements(By.className("commenting"));
		boolean found = false;
		for (WebElement c : el) {
			if (c.getText().equals("REPLY MESSAGE")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
	
	@Test
	public void testSimpleComment() {

		System.out.println(" Integration Test testSimpleComment  ");
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
		driver.findElement(By.id("textContentField")).sendKeys("HELLO WORLD!");
		driver.findElement(By.id("post-comment")).click();
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
		String commentTitle = "Hello WORLD!!";
		String commentStr = commentTitle;
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
				driver.findElement(By.id("textContentField")).sendKeys(commentStr);
		// POST
		driver.findElement(By.id("post-comment")).click(); 
		(new WebDriverWait(driver, 30))
		.until(ExpectedConditions.presenceOfElementLocated(By
				.className("ugc-comment-stream")));
		driver.findElement(By.className("like")).click();
		(new WebDriverWait(driver, 30))
		.until(ExpectedConditions.presenceOfElementLocated(By
				.className("ugc-comment-stream")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		String likes = driver.findElement(By.className("like")).getAttribute("innerText");
		assertTrue(Integer.parseInt(likes) == 1);
	}
	
	@Test
	public void testFlag() {
		System.out.println(" Integration Test testFlag");
		String commentTitle = "Hello WORLD!!";
		String commentStr = commentTitle;
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
		driver.findElement(By.id("textContentField")).sendKeys(commentStr);
		// POST
		driver.findElement(By.id("post-comment")).click(); 
		(new WebDriverWait(driver, 30))
		.until(ExpectedConditions.presenceOfElementLocated(By
				.className("ugc-comment-stream")));
		driver.findElement(By.className("flag")).click();
		(new WebDriverWait(driver, 30))
		.until(ExpectedConditions.presenceOfElementLocated(By
				.className("ugc-comment-stream")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		String likes = driver.findElement(By.className("flag")).getAttribute("innerText");
		assertTrue(Integer.parseInt(likes) == 1);
	}

}
