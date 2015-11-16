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
            session = new WebSession("http://www.google.com");
        } catch (IOException ex) {
            Logger.getLogger(TestImageInvariants.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        session.close();
    }
    
    @Test
    public void testHasImage() {
        System.out.println(System.getProperty("user.dir"));
        String referenceImg = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/google_logo.png";
        String elementXPath = "/html/body/div/div[5]/span/center/div[1]/img";
        
        Element element = session.getElement(elementXPath);
        assertTrue(element.hasImage(referenceImg));
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
