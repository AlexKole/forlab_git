package Commons;

import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The class provides different JavaScript actions to run in browser
 * 
 * @author AlexanderK
 * 
 */
public final class JSActions {

	/**
	 * Method fires 'click' event on element with provided id attribute
	 * 
	 * @param sID
	 *            : attribute 'id'
	 * @param driver
	 *            : webdriver
	 * @return : false in case some exception occurred
	 */
	public static boolean jsClickByID(final String sID, final WebDriver driver) {
		boolean bRet = true;
		try {
			((JavascriptExecutor) driver)
					.executeScript(String
							.format("var theEvent = document.createEvent(\"MouseEvent\");"
									+ "theEvent.initMouseEvent(\"click\", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
									+ "var element = document.getElementById('%s');"
									+ "element.dispatchEvent(theEvent);", sID));

		} catch (Exception e) {
			bRet = false;
		}
		return bRet;
	}

	/**
	 * Method finds webelemt on the page by its locator and regular expression
	 * matching attribute value
	 * 
	 * @param sLocator
	 *            : string locator of element (jQuery)
	 * @param sPattern
	 *            : regular expression to match attribute value
	 * @param sAttributte
	 *            : attribute of html tag
	 * @param driver
	 *            : webdriver
	 * @return found WebElement or null if nothing found
	 */
	public static WebElement jsFindElementByAttribute(final String sLocator,
			final String sPattern, final String sAttributte,
			final WebDriver driver) {
		return (WebElement) ((JavascriptExecutor) driver).executeScript(String
				.format("var pathes = $(\"%s\");"
						+ "for (var i = 0; i <pathes.length; i++) { "
						+ "if (/%s/.test(pathes[i].getAttribute('%s')))"
						+ "return pathes[i];" + "}" + "return null;", sLocator,
						sPattern, sAttributte));
	}
	
	/**
	 * Method scrolls to provided AbstractElement
	 * @param el - WebElement to scroll to
	 * @param driver - webdriver
	 * @return false in case some exception occurred
	 */
	public static boolean jsScrollTo (final By locator, final WebDriver driver) {
		boolean bRet = true;
		int attempts = 0;
		while (attempts < 5) {
			try {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false)", driver.findElement(locator));
				break;
			} catch (StaleElementReferenceException e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (Exception e) {
				if (e.getClass() == InvocationTargetException.class)
					if (((InvocationTargetException) e)
							.getTargetException().getClass() == StaleElementReferenceException.class) {
						continue;
					}
				break;
			}
			attempts++;
		}

		return bRet;
	}
}
