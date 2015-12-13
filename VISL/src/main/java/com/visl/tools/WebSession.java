package com.visl.tools;

import com.visl.Element;
import com.visl.exceptions.InvalidDimensionsException;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebSession {
    private int      DISPLAY_NUMBER  = 99;
    private String   XVFB            = "/usr/bin/Xvfb";
    private String   XVFB_COMMAND    = XVFB + " :" + DISPLAY_NUMBER;
    private String   URL             = "http://www.google.com";
    public String   SCREENSHOT_FILENAME = "/tmp/screenshot.png";
    private WebDriver driver;
    private Process p;
    private FirefoxBinary firefox;
    
    private Logger log = Logger.getLogger(WebSession.class.getName());
    
    public WebSession(String URL) throws IOException {
        p = Runtime.getRuntime().exec(XVFB_COMMAND);
        firefox = new FirefoxBinary();
        firefox.setEnvironmentProperty("DISPLAY", ":" + DISPLAY_NUMBER);
        driver = new FirefoxDriver(firefox, null);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        System.out.println("Loading webpage");
        driver.get(URL);
        System.out.println("Loaded webpage");
        takeScreenshot();
    }
    
    public Object runJScommand(String command) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(command);
    }
    
    private void takeScreenshot() throws IOException {
        File scrFile = ( (TakesScreenshot) driver ).getScreenshotAs(OutputType.FILE);
        File outputFile = new File(SCREENSHOT_FILENAME);
        FileUtils.copyFile(scrFile, outputFile);
        outputFile.deleteOnExit();
    }
    
    /**
     * Locate an element from the given XPath and return it as a VISL element.
     * 
     * @param XPath
     * @return      A VISL-type element.
     * @throws NoSuchElementException if the element cannot be found.
     */
    public Element getElement(String XPath) {
        return getElement(XPath, 0, 0);
    }
    
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
        
        String croppedFilename = "/tmp/"+element.getTagName()+"_"+Long.toString(System.currentTimeMillis() / 1000L)+".png";
        el.setImagePath(croppedFilename);
        ImageTools.cropImage(SCREENSHOT_FILENAME, croppedFilename, x, y, width, height);
        File croppedFile = new File(croppedFilename);
        croppedFile.deleteOnExit();
        return el;        
    }
    
    public Element getElement(int x, int y, int width, int height) throws NoSuchElementException, FileNotFoundException, InvalidDimensionsException {
        
        String name = x+"_"+y+"_"+width+"_"+height;
        
        Element el = new Element();
        el.setPositionAndSize(x, y, width, height);
        
        String croppedFilename = "/tmp/"+name+"_"+Long.toString(System.currentTimeMillis() / 1000L)+".png";
        el.setImagePath(croppedFilename);
        ImageTools.cropImage(SCREENSHOT_FILENAME, croppedFilename, x, y, width, height);
        
        return el;
    }
    
    public void close() {
        driver.close();
        p.destroy();        
    }
}