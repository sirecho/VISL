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
import com.visl.TestPage;

/**
 *
 * @author echo
 */
public class TestImageInvariants {
    private WebSession session;
        
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        System.out.println("Setting up a new session");
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
        final String WRONG_IMG_PATH = "/dev/null/abc.png";
        
        // TEST CASES:
        // 1. test with matching image
        // 2. test with non-matching image that is smaller than reference
        // 3. test with non-matching image that is larger than reference
        // 4. test with wrong image path
        // 5. test with wrong xpath
        // 6. test with matching image, but reference image is grayscale
        // 7. test with matching image, but reference image is brighter
        // 8. test with matching image, but reference image is cropped
        
        String referenceImg = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/google_logo.png";
        String elementXPath = "/html/body/div/div[5]/span/center/div[1]/img";
        
        Element element = session.getElement(elementXPath);
        assertTrue(element.hasImage(referenceImg));
    }
    
    @Ignore
    @Test
    public void testHasImageSize() {
        String referenceImg = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/google_logo.png";
        String elementXPath = "/html/body/div/div[5]/span/center/div[1]/img";
        
        Element element = session.getElement(elementXPath, 500, 500);
        assertTrue(element.hasImage(referenceImg));
    }
    
    
    @Test
    public void testHasSubImage() {
        String referenceImg = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/google_logo.png";
        String elementXPath = "/html/body/div/div[5]/span/center/div[1]/img";
        
        Element element = session.getElement(elementXPath, 500, 500);
        assertTrue(element.hasSubImage(referenceImg));
    }    

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
