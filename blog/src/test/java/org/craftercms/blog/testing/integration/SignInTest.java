package org.craftercms.blog.testing.integration;


import org.craftercms.blog.testing.IntegrationTestingBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SignInTest extends IntegrationTestingBase {
	
	@Test
	public void signInTest() {
		System.out.println(" Integration Test signInTest  ");
		WebDriver driver = getDriver();
		driver.get(baseUrl + WEB_APP_URL);
		WebElement siginLink = driver.findElement(By.cssSelector("a[id='signin']"));
		siginLink.click();
		loginAsAdmin(driver);
		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Social Blog"));
	}
	

}
