/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl.tools;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TextToolsTest {
    
    private static String projectDir;
    private static String resourcesDir;
    
    public TextToolsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        projectDir = System.getProperty("user.dir");
        resourcesDir = projectDir+"/src/test/resources/org/visl";
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

    /**
     * Test of getTextFromImage method, of class TextTools.
     */
    @Test
    public void testGetTextFromImage() {
        System.out.println("getTextFromImage");
        String imagePath = resourcesDir+"/text.png";
        
        String expResult =  "Tomorrow, and\n" +
                            "tomorrow, and\n" +
                            "tomorrow; creeps\n" +
                            "in this petty pace\n" +
                            "from day to day\n" +
                            "until the last syll-\n" +
                            "able of recorded\n" +
                            "time. And all our\n" +
                            "yesterdays have\n" +
                            "lighted fools the\n" +
                            "way to dusty";
        
        String result = TextTools.getTextFromImage(imagePath);
        float avgLsDistance = (float)TextTools.getLevenstheinDistance(expResult, result)/result.length();
        System.out.println("Average Levensthein distance: "+avgLsDistance);
        assertTrue("Distance was "+avgLsDistance, avgLsDistance < 0.2);
    }

    /**
     * Test of getLevenstheinDistance method, of class TextTools.
     */
    @Test
    public void testGetLevenstheinDistance() {
        System.out.println("getLevenstheinDistance");
        String a = "kitten";
        String b = "sitting";
        int expResult = 3;
        int result = TextTools.getLevenstheinDistance(a, b);
        assertEquals(expResult, result);
    }

    /**
     * Test of getLines method, of class TextTools.
     */
    @Test
    public void testGetLines() throws Exception {
        System.out.println("getLines");
        String imagePath = resourcesDir+"/yellow_on_red4.png";

        ArrayList<ArrayList<Word>> result = TextTools.getLines(imagePath);
        
        assertEquals(4, result.size());
    }

    /**
     * Test of getAlignments method, of class TextTools.
     */
    @Test
    public void testGetAlignments() {
        System.out.println("getAlignments");
        String imagePath = resourcesDir+"/yellow_on_red4.png";
        
        ArrayList<TextTools.Alignment> alignments = TextTools.getAlignments(imagePath);
        assertEquals(TextTools.Alignment.Left, alignments.get(0));
    }
}
