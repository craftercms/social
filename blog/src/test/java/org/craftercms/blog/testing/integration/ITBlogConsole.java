package org.craftercms.blog.testing.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.craftercms.blog.testing.IntegrationTestingBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ITBlogConsole extends IntegrationTestingBase {
	
	@Test
	public void testDeleteEntry() {

		System.out.println(" Integration Test testDeleteEntry ");
		
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login")).click();
		
		WebElement t = driver.findElement(By.id("blogconsole"));
		t.click();
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("#mytable")));
		
		// CHANGED driver.findElement(By.cssSelector("#detailEntryBtn")).click();
		driver.findElement(By.name(this.currentUGCId)).click();
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("#textContentField_ifr")));

		driver.findElement(By.className("deleteEntry")).click();
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("#mytable")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}

		boolean found = false;
		try {
			WebElement we = driver.findElement(By.name(this.currentUGCId));
			if(we != null) {
				found = true;
			}
		} catch (Exception e) {
			System.out.println(" ERROR EXPECTED " + e.getMessage());
		}
		assertEquals(false, found);
	}
	
	@Test
	public void testUpdateEntry() {
		System.out.println(" Integration Test testUpdateEntry ");
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
		WebElement t = driver.findElement(By.id("blogconsole"));
		t.click();
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("#mytable")));
		// CHANGED driver.findElement(By.cssSelector("#detailEntryBtn")).click();
		driver.findElement(By.name(this.currentUGCId)).click();
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("#textContentField_ifr")));
		driver.findElement(By.className("editEntry")).click();
		JavascriptExecutor exec = (JavascriptExecutor) driver;
		try {
			exec.executeScript("var _tmp = tinymce.get('textContentField'); _tmp.setContent('Hello World!'); _tmp.save();");
		} catch (Exception e) {
			System.out.println(" EROR " + e.getMessage());
		}
		new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return !d.findElement(By.cssSelector("#textContentField"))
						.getAttribute("value").isEmpty();
			}
		});
		String newTitle = "Updated";
		driver.findElement(By.id("titleEntry")).clear();
		driver.findElement(By.id("titleEntry")).sendKeys(newTitle);
		assertEquals(newTitle, driver.findElement(By.id("titleEntry"))
				.getAttribute("value"));
		driver.findElement(By.className("updateEntry")).click();
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("#viewBlog")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		String text = driver.findElement(By.name(this.currentUGCId)).getAttribute("innerHTML");
		assertEquals(newTitle, text);
	}

	@Test
	public void testNewNetry() {
		
		System.out.println(" Integration Test testNewNetry  ");
		WebDriver driver = getDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + WEB_APP_URL);
		driver.findElement(By.id("signin")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login")).click();
		WebElement t = driver.findElement(By.id("blogconsole"));
		t.click();
		driver.findElement(By.id("newEntry")).click();
		(new WebDriverWait(driver, 30))
				.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("#textContentField_ifr")));
		JavascriptExecutor exec = (JavascriptExecutor) driver;
		try {
			exec.executeScript("var _tmp = tinymce.get('textContentField'); _tmp.setContent('Hello World!'); _tmp.save();");
		} catch (Exception e) {
			System.out.println(" EROR " + e.getMessage());
		}
		new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return !d.findElement(By.cssSelector("#textContentField"))
						.getAttribute("value").isEmpty();
			}
		});
		driver.findElement(By.id("titleEntry")).clear();
		driver.findElement(By.id("titleEntry")).sendKeys("test");
		WebElement textContent = driver.findElement(By.id("textContentField"));
		assertEquals("<p>Hello World!</p>", textContent.getAttribute("value"));
		driver.findElement(By.id("saveEntry")).click();
		(new WebDriverWait(driver, 30))
		.until(ExpectedConditions.presenceOfElementLocated(By
				.cssSelector("#mytable")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		List<WebElement> pl = driver.findElements(By.id("detailEntryBtn"));
		boolean found = false;
		String title;
		for (WebElement webElement : pl) {
			title = webElement.getAttribute("innerHTML");
			if (title.equals("test")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}


}
