package org.craftercms.blog.testing;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.craftercms.profile.impl.ProfileRestClientImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

public class IntegrationTestingBase {
	
	protected static final String WEB_APP_URL = "/crafter-social-blog-demo";
	
	protected final transient Logger log = LoggerFactory.getLogger(IntegrationTestingBase.class);

	private WebDriver mDriver = null;
	private boolean mAutoQuitDriver = true;

	protected String currentUGCId;

	protected static Properties sConfig;
	protected static DesiredCapabilities sCaps;

	protected static String baseUrl;

	private static final String CONFIG_FILE = "test.properties";
	
	protected static ProfileRestClientImpl client; 
	
	private static String appToken;
	protected static String ticket;

	@BeforeClass
	public static void configure() throws IOException {
		// Read config file
		sConfig = new Properties();
		// sConfig.load(new FileReader(CONFIG_FILE));
		sConfig.load(IntegrationTestingBase.class.getClassLoader().getResourceAsStream(
				CONFIG_FILE));
		baseUrl = sConfig.getProperty("craftercms.test.base.url");
		int port = Integer.parseInt(sConfig.getProperty("craftercms.test.port"));
		RestAssured.port = port;
		client = new ProfileRestClientImpl();
		client.setPort(port);
		try {
			appToken = client.getAppToken("craftersocial", "craftersocial");
			//ticket = client.getTicket(appToken, "admin", "admin", "craftercms");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Before
	public void prepareDriver() throws Exception {
		ticket = client.getTicket(appToken, "admin", "admin", "craftercms");
		mDriver = new FirefoxDriver();
		try {
			createNewPost(ticket, "craftercms");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(" NEW POST ERROR " + e.getMessage());
		}
	}

	protected WebDriver getDriver() {
		return mDriver;
	}

	protected void disableAutoQuitDriver() {
		mAutoQuitDriver = false;
	}

	protected void enableAutoQuitDriver() {
		mAutoQuitDriver = true;
	}

	protected boolean isAutoQuitDriverEnabled() {
		return mAutoQuitDriver;
	}

	@After
	public void quitDriver() {
		try {
			WebElement signout = mDriver.findElement(By.id("signout"));
			if (signout != null) {
				signout.click();
			}
		} catch (Exception e) {
			
		}
		if (mAutoQuitDriver && mDriver != null) {
			mDriver.quit();
			mDriver = null;
		}
	}
	
	protected void loginAsAdmin(WebDriver driver) {
		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Social Blog - Login"));
		WebElement inputUsername = driver.findElement(By.id("username")); 
		WebElement inputPass = driver.findElement(By.id("password")); 
		WebElement loginButton = driver.findElement(By.id("login")); 
		inputUsername.sendKeys("admin");
		inputPass.sendKeys("admin");
		loginButton.click();
	}
	
	protected String createNewPost(String ticket, String tenant) {
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
		return currentUGCId;
	}
	
	
	
	
}
