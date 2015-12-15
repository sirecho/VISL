package com.visl.tests;

import com.visl.Constants;
import com.visl.Element;
import com.visl.TestPage;
import com.visl.exceptions.InvalidDimensionsException;
import com.visl.tools.WebSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.openqa.selenium.NoSuchElementException;

/**
 * This class contains tests based on the specifications of the visual invariants.
 */
public class TestImageInvariants {

    private static String projectDir;
    private static String webDir;
    private static String imgDir;
    private static String searocksImg;
    private static String searocksXPath;
    private static String owlImg;
    
    private static WebSession session;
    private static Logger log;
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        projectDir = System.getProperty("user.dir");
        webDir = projectDir+"/"+Constants.WEB_DIR;
        imgDir = projectDir+"/"+Constants.IMAGES_DIR;
        
        searocksImg = webDir+TestPage.SEAROCKS_PNG;
        searocksXPath = TestPage.SEAROCKS_XPATH;
        owlImg = webDir+TestPage.OWL_PNG;
        
        log = Logger.getLogger(TestImageInvariants.class.getName());
        
        log.log(Level.INFO, "Setting up a new session");
        session = new WebSession(TestPage.URL);
    }
    
    @AfterClass
    public static void tearDownClass() {
        session.close();
    }
    
    @Before
    public void setUp() {

    }
    
    @After
    public void tearDown() {
        
    }
    
    /**
     * Test with matching image.
     */
    @Test
    public void matching() { 
        System.out.println("Testing with matching image");
        Element element = session.getElement(searocksXPath);
        assertTrue(element.hasExactImage(searocksImg));
    }
    
    /**
     * Test with image that is an enlarged version of the element's image.
     */
    @Test
    public void matching_resized() {
        System.out.println("Testing with matching enlarged image");
        Element element = session.getElement(searocksXPath);
        assertTrue(element.hasExactResizedImage(imgDir+Constants.SEAROCKS_MEDIUM_PNG, 300, 200));
    }
    
    /**
     * Test with image that is a nonmatching enlarged version of the element's image.
     */
    @Test
    public void nonmatching_resized() {
        System.out.println("Testing with nonmatching enlarged image");
        Element element = session.getElement(searocksXPath);
        assertFalse(element.hasExactResizedImage(imgDir+Constants.OWL_LARGE_JPG, 300, 200));
    }    
    
    /**
     * Test with non-matching image that is smaller than the element's image.
     */
    @Test
    public void nonmatching_smaller() {
        System.out.println("Testing with smaller non-matching image");
        Element element = session.getElement(searocksXPath);
        String smallerNonMatchImg = imgDir+Constants.FACE_JPG;
        assertFalse(element.hasExactImage(smallerNonMatchImg));
    }
    
    /**
     * Test with non-matching image that is larger than the element's image. 
     */
    @Test
    public void nonmatching_larger() {
        System.out.println("Testing with larger non-matching image");
        Element element = session.getElement(searocksXPath);
        String largerNonMatchImg = imgDir+Constants.SEAROCKS_LARGE_JPG;
        assertFalse(element.hasExactImage(largerNonMatchImg));
    }
    
    /**
     * Test with wrong image path. 
     */
    @Test
    public void wrongFilepath() {
        System.out.println("Testing with wrong image path");
        Element element = session.getElement(searocksXPath);
        assertFalse(element.hasExactImage("/dev/null"));
    }
    
    /**
     * Test with invalid XPath.
     */
    @Test(expected = NoSuchElementException.class)
    public void invalidXPath() {
        System.out.println("Testing with invalid XPath");
        session.getElement("asdjn23823!\\)");
    }
    
    /**
     * Test with wrong XPath.
     */
    @Test(expected = NoSuchElementException.class)
    public void wrongXPath() {
        System.out.println("Testing with wrong XPath");
        session.getElement("//body/font/li/null");
    }
    
    /**
     * Test with matching image, but reference image is grayscale.
     */
    @Test
    public void grayscale() {
        System.out.println("Testing with matching greyscale image");
        Element element = session.getElement(searocksXPath);
        String searocksGreyImg = imgDir+Constants.SEAROCKS_GRAYSCALE_PNG;
        assertFalse(element.hasExactImage(searocksGreyImg));   
    }
    
    /**
     * Test with matching image, but reference image is brighter.
     */
    @Test
    public void brighter() {
        System.out.println("Testing with matching brighter image");
        Element element = session.getElement(searocksXPath);
        String searocksBrightImg = imgDir+Constants.SEAROCKS_BRIGHT_PNG;
        assertFalse(element.hasExactImage(searocksBrightImg));
    }
    
    /**
     * Test with matching image, but reference image is cropped.
     */
    @Test
    public void cropped() {
        System.out.println("Testing with matching cropped image");
        Element element = session.getElement(searocksXPath);
        String searocksCroppedImg = imgDir+Constants.SEAROCKS_CROPPED_PNG;
        assertFalse(element.hasExactImage(searocksCroppedImg));
    }
    
    /**
     * Test with matching image, but reference image is resized.
     */
    @Test
    public void scaled() {
        System.out.println("Testing with matching scaled image");
        Element element = session.getElement(searocksXPath);
        String searocksScaledImg = imgDir+Constants.SEAROCKS_SCALED_PNG;
        assertFalse(element.hasExactImage(searocksScaledImg));
    }
    
    
    /**
     * Test with matching image and correct size.
     */
    @Test
    public void size_correct() {
        System.out.println("Testing hasImage with specific size and matching image");
        Element element = session.getElement(searocksXPath, 300, 200);
        assertTrue(element.hasExactImage(searocksImg));
    }
    
    /**
     * Test with matching image and smaller size.
     */
    @Test
    public void size_smaller() {
        System.out.println("Testing with matching image and smaller size");
        Element element = session.getElement(searocksXPath, 30, 20);
        assertFalse(element.hasExactImage(searocksImg));        
    }
    
    /**
     * Test with matching image and larger size.
     */
    @Test
    public void size_larger() {
        System.out.println("Testing with matching image and larger size");
        Element element = session.getElement(searocksXPath, 600, 400);
        assertFalse(element.hasExactImage(searocksImg));        
    }
    
    /**
     * Test with matching image and out-of-bounds size.
     */
    @Test(expected = InvalidDimensionsException.class)
    public void size_overflow() {
        System.out.println("Testing with matching image and out-of-bounds size");
        session.getElement(searocksXPath, 30000, 20000);     
    }
    
    /**
     * Test with matching image and negative size.
     */
    @Test(expected = InvalidDimensionsException.class)
    public void size_negative() {
        System.out.println("Testing with matching image and negative size");
        session.getElement(searocksXPath, -3, -2);    
    }
    
    /**
     * Test with image that is a subset of the element's image
     */
    @Test
    public void subImage_matching() {
        System.out.println("Testing hasSubImage with matching image");
        Element element = session.getElement(TestPage.CONTENT_XPATH);
        assertTrue(element.containsImage(searocksImg));
    }
    
    /**
     * Test with image that is an enlarged version of the element's image.
     */
    @Test
    public void subImage_matching_resized() {
        System.out.println("Testing with matching enlarged image");
        Element element = session.getElement(TestPage.CONTENT_XPATH);
        assertTrue(element.containsResizedImage(imgDir+Constants.SEAROCKS_MEDIUM_PNG, 300, 200));
    }    
    
    @Test
    public void matching_location() {
        System.out.println("Testing location with matching image");
        Element element = session.getElementByLocation("Top Left");
        assertTrue(element.containsImage(owlImg));
    }
}
