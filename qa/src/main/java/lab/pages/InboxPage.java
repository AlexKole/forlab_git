package lab.pages;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import logger.MainLogger;


/**
 * Class represents Inbox page.
 * 
 * @author AlexanderK
 * 
 */
public class InboxPage extends AbstractPage{

	@FindBy(css = "a[href*='inbox']")
	public WebElement btnInbox;
	
	public InboxPage(WebDriver driver) {
		super(driver);
		try {
			btnInbox.click();
		} catch (NoSuchElementException e) {
			MainLogger.getInstance().error(
					"Button Menu/Inbox is not found", true);
		}

		verifyOpened("Входящие");
	}

}

