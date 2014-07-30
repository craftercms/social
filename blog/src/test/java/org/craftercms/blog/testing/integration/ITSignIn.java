package org.craftercms.blog.testing.integration;


import org.craftercms.blog.testing.IntegrationTestingBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class ITSignIn extends IntegrationTestingBase {
	
	@Test
	public void signInTest() {
		System.out.println(" Integration Test signInTest 1 ");
		WebDriver driver = getDriver();
		driver.get(baseUrl + WEB_APP_URL);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		WebElement siginLink = driver.findElement(By.cssSelector("a[id='signin']"));
		siginLink.click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		loginAsAdmin(driver);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(" WAITING ERROR");
		}
		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Social Blog"));
	}
	

}
