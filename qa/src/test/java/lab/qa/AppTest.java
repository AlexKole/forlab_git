package lab.qa;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

import lab.pages.ComposePage;
import logger.MainLogger;


public class AppTest {

	private WebDriver driver, driver1;
	private String baseUrl;
	private MainLogger log;

	@BeforeClass
	public static void setUpSuite() {
		MainLogger log = MainLogger.getInstance();
		log.setFileHandler("AppTest_Suite.xml");
		log.suiteIn("Test Suite. Just check");

	}

	@AfterClass
	public static void exitSuite() {
		MainLogger.getInstance().suiteOut();
	}

	@Before
	public void setUp() throws Exception {
		log = MainLogger.getInstance();
		driver = new InternetExplorerDriver();
		driver.manage().timeouts()
				.implicitlyWait(Constants.iImplicitTimeout, TimeUnit.SECONDS);
		baseUrl = "https://mail.ru";
	}

	@Test
	public void ATC_01_SendEmail() throws Exception {
		log.testIn("Test1. Log in and send");
		log.stepIn("Step_1. Log in");
			MainWindow mainwin = new MainWindow(driver, baseUrl);
		log.stepOut();
		
		log.stepIn("Step_2. Open Compose page");
			ComposePage compose = (ComposePage) mainwin.openPage(ComposePage.class);
		log.stepOut();

		log.stepIn("Step_2. Send a letter");
			compose.send("alex.kole625@gmail.com", "Test subj", "Test body");
		log.stepOut();		

	}

	@After
	public void tearDown() throws Exception {
		log.testOut();
		driver.quit();
	}
}

