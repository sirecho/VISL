/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl;

import com.visl.tools.WebSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import com.visl.Constants;
import com.visl.exceptions.InvalidDimensionsException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import org.opencv.core.Core;
import org.openqa.selenium.NoSuchElementException;

public class TestImageInvariants {

    private static String projectDir;
    private static String webDir;
    private static String imgDir;
    
    private WebSession session;
    private static Logger log;
        
    @BeforeClass
    public static void setUpClass() {
        projectDir = System.getProperty("user.dir");
        webDir = projectDir+"/"+Constants.WEB_DIR;
        imgDir = projectDir+"/"+Constants.IMAGES_DIR;


        
        log = Logger.getLogger(TestImageInvariants.class.getName());
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    
    @Before
    public void setUp() {
        log.log(Level.INFO, "Setting up a new session");
        try {      
            session = new WebSession(TestPage.URL);
        } catch (IOException ex) {
            Logger.getLogger(TestImageInvariants.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        session.close();
    }
    
    @Ignore
    @Test
    public void testHasImage() {
         
        // 1. test with matching image
        System.out.println("Testing with matching image");
        String searocksImg = webDir+TestPage.SEAROCKS_PNG;
        String searocksXPath = TestPage.SEAROCKS_XPATH;
        Element element = session.getElement(searocksXPath);
        assertTrue(element.hasImage(searocksImg));
        
        // 2. test with non-matching image that is smaller than reference
        System.out.println("Testing with smaller non-matching image");
        String smallerNonMatchImg = imgDir+Constants.FACE_JPG;
        assertFalse(element.hasImage(smallerNonMatchImg));
        
        // 3. test with non-matching image that is larger than reference
        System.out.println("Testing with larger non-matching image");
        String largerNonMatchImg = imgDir+Constants.SEAROCKS_LARGE_JPG;
        assertFalse(element.hasImage(largerNonMatchImg));
        
        // 4. test with wrong image path
        System.out.println("Testing with wrong image path");
        assertFalse(element.hasImage("/dev/null"));
        
        // 5. test with invalid xpath
        System.out.println("Testing with invalid XPath");
        boolean caughtException = false;
        try {
            session.getElement("asdjn23823!\\)");
        } catch (NoSuchElementException ex) {
            caughtException = true;
        }
        assertTrue("Invalid XPath did not throw exception!", caughtException);
        
        // 6. test with wrong xpath
        System.out.println("Testing with wrong XPath");
        caughtException = false;
        try {
            session.getElement("//body/font/li/null");
        } catch (NoSuchElementException ex) {
            caughtException = true;
        }
        assertTrue("Invalid XPath did not throw exception!", caughtException);
        
        // 7. test with matching image, but reference image is grayscale
        System.out.println("Testing with matching greyscale image");
        String searocksGreyImg = imgDir+Constants.SEAROCKS_GREYSCALE_PNG;
        assertFalse(element.hasImage(searocksGreyImg));
        
        // 8. test with matching image, but reference image is brighter
        System.out.println("Testing with matching brighter image");
        String searocksBrightImg = imgDir+Constants.SEAROCKS_BRIGHT_PNG;
        assertFalse(element.hasImage(searocksBrightImg));
        
        // 9. test with matching image, but reference image is cropped
        System.out.println("Testing with matching cropped image");
        String searocksCroppedImg = imgDir+Constants.SEAROCKS_CROPPED_PNG;
        assertFalse(element.hasImage(searocksCroppedImg));
        
        // 10. test with matching image, but reference image is resized
        System.out.println("Testing with matching cropped image");
        String searocksScaledImg = imgDir+Constants.SEAROCKS_SCALED_PNG;
        assertFalse(element.hasImage(searocksScaledImg));
    }
    
    
    @Test
    public void testHasImageSize() {
        
        // 1. test with matching image and correct size
        System.out.println("Testing with matching image");
        String searocksImg = webDir+TestPage.SEAROCKS_PNG;
        String searocksXPath = TestPage.SEAROCKS_XPATH;
        Element element = session.getElement(searocksXPath, 300, 200);
        assertTrue(element.hasImage(searocksImg));
        
        // 2. test with matching image and smaller size
        System.out.println("Testing with matching image and smaller size");
        element = session.getElement(searocksXPath, 30, 20);
        assertFalse(element.hasImage(searocksImg));
        
        // 3. test with matching image and larger size
        System.out.println("Testing with matching image and larger size");
        element = session.getElement(searocksXPath, 600, 400);
        assertFalse(element.hasImage(searocksImg));
        
        // 4. test with matching image and out-of-bounds size
        System.out.println("Testing with matching image and out-of-bounds size");
        boolean caughtException = false;
        try {
            session.getElement(searocksXPath, 30000, 20000);
        } catch (InvalidDimensionsException ex) {
            caughtException = true;
        }
        assertTrue("Invalid dimensions did not throw exception!", caughtException);
        
        // 5. test with matching image and negative size
        System.out.println("Testing with matching image and negative size");
        try {
            session.getElement(searocksXPath, -3, -2);
        } catch (InvalidDimensionsException ex) {
            caughtException = true;
        }
        assertTrue("Invalid dimensions did not throw exception!", caughtException);
    }
    
    @Ignore
    @Test
    public void testHasSubImage() {
        String referenceImg = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/google_logo.png";
        String elementXPath = "/html/body/div/div[5]/span/center/div[1]/img";
        
        Element element = session.getElement(elementXPath, 500, 500);
        assertTrue(element.hasSubImage(referenceImg));
    }
}
