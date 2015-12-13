/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl;

import com.visl.tools.TextTools;
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
public class TestTextTools {
    
    public TestTextTools() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testImageHasText() {
        String referenceImg = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/text.png";
        String text = TextTools.getTextFromImage(referenceImg);
        
        String referenceText =  "Tomorrow, and\n" +
                                "tomorrow, and\n" +
                                "tomorrow; creeps\n" +
                                "in this petty pace\n" +
                                "from day to day,\n" +
                                "until the last syll-\n" +
                                "able of recorded\n" +
                                "time. And all our\n" +
                                "yesterdays have\n" +
                                "lighted fools the\n" +
                                "way to dusty";
        
        int ld = TextTools.getLevenstheinDistance(referenceText, text);
        System.out.println("LD: "+ld);
        assertTrue(ld < 5);
    }
    
    @Test
    public void testLevenshtein() {
        String a = "Google";
        String b = "Goøgle";
        
        int ld = TextTools.getLevenstheinDistance(a, b);
        assertTrue(ld<5);
    }
}
