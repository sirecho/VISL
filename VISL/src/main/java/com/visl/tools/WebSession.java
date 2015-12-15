package com.visl.tools;

import com.visl.Element;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebSession {
    public final String   SCREENSHOT_FILENAME = "/tmp/screenshot.png";
    private final int      DISPLAY_NUMBER  = 99;
    private final String   XVFB            = "/usr/bin/Xvfb";
    private final String   XVFB_COMMAND    = XVFB + " :" + DISPLAY_NUMBER;
    private final WebDriver driver;
    private final Process p;
    private final FirefoxBinary firefox;
    
    private static final Logger log = Logger.getLogger(WebSession.class.getName());
    
    /**
     * Initiate a new session for the given URL.
     * 
     * Creates a new session and generates a screen shot of the web page at the
     * given URL.
     * 
     * @param url The URL to the web page
     * @throws IOException 
     */
    public WebSession(String url) throws IOException {
        p = Runtime.getRuntime().exec(XVFB_COMMAND);
        firefox = new FirefoxBinary();
        firefox.setEnvironmentProperty("DISPLAY", ":" + DISPLAY_NUMBER);
        driver = new FirefoxDriver(firefox, null);
        
        // Set WebDriver timeout values
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        
        driver.get(url);
        log.log(Level.INFO, "Loaded webpage {0}", url);
        takeScreenshot();
    }
    
    /**
     * Run a JavaScript command on the webpage.
     * 
     * @param command the JS command
     * @return the output of the command
     */
    public Object runJScommand(String command) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(command);
    }
    
    /**
     * Take a screen shot of the web page and store it.
     * @throws IOException 
     */
    private void takeScreenshot() throws IOException {
        File scrFile = ( (TakesScreenshot) driver ).getScreenshotAs(OutputType.FILE);
        File outputFile = new File(SCREENSHOT_FILENAME);
        FileUtils.copyFile(scrFile, outputFile);
        outputFile.deleteOnExit();
    }
    
    /**
     * Locate an element from the given XPath and return it as a VISL element.
     * 
     * @param XPath the element's XPath
     * @return A VISL-type element.
     * @throws NoSuchElementException if the element cannot be found.
     */
    public Element getElement(String XPath) {
        return getElement(XPath, 0, 0);
    }
    
    /**
     * Locate an element from the given XPath, width and height, and return it 
     * as a VISL element.
     * 
     * Returns an element that begins at the top-left coordinate of the DOM-
     * element corresponding to the given XPath and ends at the bottom-left
     * coordinate defined by [x+width, y+height].
     * 
     * @param XPath the element's XPath
     * @param width the width of the element
     * @param height the height of the element
     * @return A VISL-type element.
     * @throws NoSuchElementException if the element cannot be found.
     */    
    public Element getElement(String XPath, int width, int height) {
        log.log(Level.INFO, "Looking for element {0}", XPath);
        
        // Look for the element
        WebElement element = (WebElement) driver.findElement(By.xpath(XPath));
        
        /*
        * Get the position and size of the element.
        * This is currently done by getting the element's BoundingClientRect,
        * as Selenium's getLocation() function does not return correct values.
        */
        int x = (int) Math.round(Double.parseDouble(((JavascriptExecutor)driver).executeScript("return arguments[0].getBoundingClientRect()[\"x\"]", element).toString()));
        int y = (int) Math.round(Double.parseDouble(((JavascriptExecutor)driver).executeScript("return arguments[0].getBoundingClientRect()[\"y\"]", element).toString()));
        
        if (width == 0 && height == 0) {
            width = (int) Math.round(Double.parseDouble(((JavascriptExecutor)driver).executeScript("return arguments[0].getBoundingClientRect()[\"width\"]", element).toString()));
            height = (int) Math.round(Double.parseDouble(((JavascriptExecutor)driver).executeScript("return arguments[0].getBoundingClientRect()[\"height\"]", element).toString()));
        }
        
        log.log(Level.INFO, "Found element at {0},{1} {2}x{3}", new Object[]{x, y, width, height});
        
        Element el = new Element(XPath);
        el.setPositionAndSize(x, y, width, height);
        
        String croppedFilename = "/tmp/"+element.getTagName()+"_"+Long.toString(System.currentTimeMillis())+".png";
        el.setImagePath(croppedFilename);
        ImageTools.cropImage(SCREENSHOT_FILENAME, croppedFilename, x, y, width, height);
        File croppedFile = new File(croppedFilename);
        croppedFile.deleteOnExit();
        log.log(Level.INFO, "Saved file {0}", croppedFilename);
        return el;        
    }
    
    /**
     * Locate an element from the given location and return it as a VISL element.
     * 
     * @param location The location string: "Top|Center|Bottom Left|Center|Right"
     * @return A VISL-type element.
     * @throws NoSuchElementException if the element cannot be found.
     */
    public Element getElementByLocation(String location) {
       
    	int screenHeight = driver.manage().window().getSize().getHeight();
    	int screenWidth = driver.manage().window().getSize().getWidth();
    	int x;
    	int y;
    	int width = screenWidth/3;
    	int height = screenHeight/3;
    	switch(location)
    	{
    	case "Top Right": 	x = 2 * screenWidth/3;
    						y = 0;
    						break;
    	case "Top Center": 	x = screenWidth/3;
    						y = 0;
    						break;
    	case "Top Left": 	x = 0;
    						y=0;
    						break;
    	case "Center Right": 	x = 2* screenWidth/3;
    							y = screenHeight/3;
    							break;
    	case "Center Center":	x = screenWidth/3;
    							y = screenHeight/3;
    							break;
    	case "Center Left": 	x = 0;
    							y = screenHeight/3;
    							break;
    	case "Bottom Right": 	x = 2 * screenWidth/3;
    							y = 2 * screenHeight/3;
    							break;
    	case "Bottom Center":	x = screenWidth/3;
    							y = 2 * screenHeight/3;
    							break;
    	case "Bottom Left": 	x = 0;
    							y = 2 * screenHeight/3;
    							break;
    	default:	x = 0;
    				y = 0;
    	}
    	
    	return getElement(x, y, width, height);
    }
        
    
    /**
     * Get a VISL element based on the given location and dimensions.
     * 
     * @param x top-left X-coordinate
     * @param y top-left Y-coordinate
     * @param width width in pixels
     * @param height height in pixels
     * @return The VISL element corresponding to the given region.
     */
    public Element getElement(int x, int y, int width, int height) {
        
        String name = x+"_"+y+"_"+width+"_"+height;
        
        Element el = new Element();
        el.setPositionAndSize(x, y, width, height);
        
        String croppedFilename = "/tmp/"+name+"_"+Long.toString(System.currentTimeMillis())+".png";
        el.setImagePath(croppedFilename);
        ImageTools.cropImage(SCREENSHOT_FILENAME, croppedFilename, x, y, width, height);
        File croppedFile = new File(croppedFilename);
        croppedFile.deleteOnExit();
        log.log(Level.INFO, "Saved file {0}", croppedFilename);
        return el;         
    }
    
    /**
     * Close the session and destroy the thread.
     */
    public void close() {
        driver.close();
        p.destroy();        
    }
}