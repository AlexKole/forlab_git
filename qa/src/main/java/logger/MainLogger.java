package logger;

import static org.junit.Assert.assertFalse;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * The class realizes logging and exit conditions of tests run
 * 
 * @author AlexanderK
 * 
 */
public class MainLogger {

	private static MainLogger INSTANCE;
	private static Logger logger = Logger.getLogger("MainLogger");
	private String sStepError = "Success";
	private String sTestError = "Success";
	private int iFailedTestsCount;
	private int iPassedTestsCount;
	private int iTotalTestsCount;
	private boolean bExit = false;
	private boolean bStepExecuting;
	private StringBuffer TestBuffer;
	private StringBuffer StepBuffer;
	private String sCurrentTest;
	private String sCurrentStep;
	private FileHandler fh = null;
	private CleanMessageFormatter MessageLog;

	public static MainLogger getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new MainLogger();
		}
		return INSTANCE;
	}

	public MainLogger() {
		TestBuffer = new StringBuffer(500);
		StepBuffer = new StringBuffer(50);
		MessageLog = new CleanMessageFormatter();
	}

	/**
	 * Method switches logging to provided file
	 * 
	 * @param sFileName
	 *            : file name to log tests run
	 */
	public void setFileHandler(final String sFileName) {
		if (null != fh) {
			logger.removeHandler(fh);
			fh.close();

		}

		try {
			fh = new FileHandler(sFileName);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.addHandler(fh);
	}

	/**
	 * Method logs error and exit/contionue execution depending on bExit
	 * 
	 * @param sMsg
	 *            : message to log
	 * @param bExit
	 *            : true - exit; false - continue
	 */
	public void error(final String sMsg, final boolean bExit) {
		StepBuffer.append(String.format("<error message=\"%s\" screenshot=\"%s\"/>", sMsg.replaceAll("\"","'"), captureScreen(sCurrentTest + "___" + sCurrentStep 
				+ "__" + new SimpleDateFormat("mmssSSS").format(new Date(System.currentTimeMillis()))).getAbsolutePath()));
		sStepError = "Failed";
		sTestError = "Failed";
		this.bExit = bExit;
		if (bExit)
			stepOut();
	}

	/**
	 * Method logs info message
	 * 
	 * @param sMsg
	 *            : message to log
	 */
	public void info(final String sMsg) {
		StepBuffer.append(String.format("<info message=\"%s\"/>", sMsg));
	}

	/**
	 * Method logs warning
	 * 
	 * @param sMsg
	 *            : message to log
	 * @param bExit
	 *            : true - exit; false - continue
	 */
	public void warn(final String sMsg) {
		StepBuffer.append(String.format("<warning message=\"%s\"/>", sMsg));
		sStepError = "Warning";
		sTestError = "Warning";
	}

	/**
	 * Method starts logging testsuite execution
	 * 
	 * @param sTest
	 *            : test suite name
	 */
	public void suiteIn(final String sSuite) {
		fh.setFormatter(MessageLog);
		String[] message = sSuite.split("\\.", 2);
		logger.info(String.format("<testset Name=\"%s\" Description=\"%s\">",
				message[0], message[1].trim()));
		iTotalTestsCount = 0;
		iPassedTestsCount = 0;
		iFailedTestsCount = 0;
	}

	/**
	 * Method stops logging testsuite execution
	 */
	public void suiteOut() {
		fh.setFormatter(MessageLog);
		logger.info(String.format(
				"<statistics Total=\"%d\" Passed=\"%d\" Failed=\"%d\"/>",
				iTotalTestsCount, iPassedTestsCount, iFailedTestsCount));
		logger.info("</testset>");
	}

	/**
	 * Method starts logging testcase execution
	 * 
	 * @param sTest
	 *            : test case name
	 */
	public void testIn(final String sTest) {
		fh.setFormatter(MessageLog);
		String[] message = sTest.split("\\.", 2);
		logger.info(String.format(
				"<testcase Name=\"%s\" Description=\"%s\" Status=\"",
				message[0], message[1].trim()));
		sCurrentTest = message[0];
		iTotalTestsCount++;
	}

	/**
	 * Method stops logging current testcase execution
	 */
	public void testOut() {
		if (this.bStepExecuting) // in case some not logged exception occurred
									// will correctly close the step
		{
			error("Internal error occured", false);
			stepOut();
		}
		fh.setFormatter(MessageLog);
		logger.info(String.format("%s\">", sTestError));
		logger.info(TestBuffer.toString());
		logger.info("</testcase>");
		TestBuffer.setLength(0);
		if ("Failed" == sTestError)
			iFailedTestsCount++;
		else
			iPassedTestsCount++;
		sTestError = "Success";
	}

	/**
	 * Method starts logging of test step
	 * 
	 * @param sStep
	 *            : format - "Step_1. Description"
	 */
	public void stepIn(final String sStep) {
		this.bExit = false;
		this.bStepExecuting = true;
		StepBuffer.setLength(0);
		String[] message = sStep.split("\\.", 2);
		sCurrentStep = message[0];
		TestBuffer.append(String.format(
				"<step Name=\"%s\" Description=\"%s\" Status=\"", sCurrentStep,
				message[1].trim().replaceAll("\"","'")));

	}

	/**
	 * Method starts logging of test step
	 * 
	 * @param sStepDescription
	 *            : format - "Step_1. Description"
	 * @param sStepExpected: Expected result
	 */
	public void stepIn(final String sStepDescription, final String sStepExpected) {
		this.bExit = false;
		this.bStepExecuting = true;
		StepBuffer.setLength(0);
		String[] message = sStepDescription.split("\\.", 2);
		sCurrentStep = message[0];
		TestBuffer.append(String.format(
				"<step Name=\"%s\" Description=\"%s\" Expected=\"%s\" Status=\"", sCurrentStep,
				message[1].trim().replaceAll("\"","'"), sStepExpected));

	}
	/**
	 * Method ends logging of current test step
	 */
	public void stepOut() {
		TestBuffer.append(String.format("%s\">", sStepError));
		TestBuffer.append(StepBuffer.toString());
		TestBuffer.append("</step>");
		sStepError = "Success";
		StepBuffer.setLength(0);
		this.bStepExecuting = false;
		assertFalse("Exit test case", bExit);
	}

	
	/**
	 * Method ends logging of current test step with exception
	 */
	public void exOut() {
		TestBuffer.append("\">");
		TestBuffer.append(StepBuffer.toString());
		TestBuffer.append(String.format("%s]]></error>", sStepError));
		TestBuffer.append("</step>");
		sStepError = "Success";
		StepBuffer.setLength(0);
		this.bStepExecuting = false;
		assertFalse("Exit test case", true);
	}
	
	
	/**
	 * Method logs exception surrounded with CDATA section and exit execution
	 * 
	 * @param sMsg : message to log
	 */
	public void exception(final String sMsg) {
		StepBuffer.append(String.format("<error message=\"Exception occured!\"><![CDATA[%s", sMsg.replaceAll("\"","'")));
		sStepError = "Failed";
		sTestError = "Failed";
		exOut();
	}
	
	/**
	 * Gets current test case name
	 * 
	 * @return : current test case name
	 */
	public String getTestName() {
		return sCurrentTest;
	}

	/**
	 * Gets current test step name
	 * 
	 * @return : current test step name
	 */
	public String getTestStep() {
		return sCurrentStep;
	}

	/**
	 * This Formatter formats the given log record to paste pure message
	 */
	class CleanMessageFormatter extends Formatter {
		@Override
		public synchronized String format(LogRecord record) {
			StringBuffer buffer = new StringBuffer(50);
			buffer.append(record.getMessage());
			return buffer.toString();
		}
	}
	
	/**
	 * Method captures screenshot to <ScreenShots> folder
	 * @param fileName : filename to keep screenshot
	 * @return : File object of saved screenshot
	 */
	public File captureScreen(String fileName) {
		File theDir = new File("ScreenShots");
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		     } catch(SecurityException se){
		    	 System.out.println(se);
		     }        
		}
		File out = new File("ScreenShots/" + fileName + ".png");
		try {
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			ImageIO.write(capture, "png", out);
			} catch (IOException ex) {
				System.out.println(ex);
				} catch (AWTException ex) {
					System.out.println(ex);
					}
		return out;
	}
	
}
