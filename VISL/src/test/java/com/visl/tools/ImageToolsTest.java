///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.visl.tools;
//
//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import org.opencv.core.Core;
//
///**
// *
// * @author echo
// */
//public class ImageToolsTest {
//    
//    private static String projectDir;
//    private static String resourcesDir;
//    
//    public ImageToolsTest() {
//    }
//    
//    @BeforeClass
//    public static void setUpClass() {
//        projectDir = System.getProperty("user.dir");
//        resourcesDir = projectDir+"/src/test/resources/org/visl";
//        
//        // Load OpenCV library
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Before
//    public void setUp() {
//    }
//    
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of compareImages method, of class ImageTools.
//     */
//    @Test
//    public void testCompareImages() throws Exception {
//        System.out.println("compareImages");
//        System.out.println(System.getProperty("java.library.path"));
//        String pathFirst = resourcesDir+"/google_logo.png";
//        String pathTwo = resourcesDir+"/google_logo.png";
//        System.out.println("Comparing images "+pathFirst+" and "+pathTwo);
//        float expResult = 1.0F;
//        float result = ImageTools.compareImages(pathFirst, pathTwo);
//        assertEquals(expResult, result, 0.0);
//    }
//
//    /**
//     * Test of hasSubImage method, of class ImageTools.
//     */
//    @Test
//    public void testHasSubImage() {
//        System.out.println("hasSubImage");
//        String source = "";
//        String template = "";
//        boolean expResult = false;
//        boolean result = ImageTools.hasSubImage(source, template, false);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of cropImage method, of class ImageTools.
//     */
//    @Test
//    public void testCropImage() {
//        System.out.println("cropImage");
//        String inPath = "";
//        String outPath = "";
//        int x = 0;
//        int y = 0;
//        int w = 0;
//        int h = 0;
//        boolean expResult = false;
//        boolean result = ImageTools.cropImage(inPath, outPath, x, y, w, h);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getImageColors method, of class ImageTools.
//     */
//    @Test
//    public void testGetImageColors() {
//        System.out.println("getImageColors");
//        String imgPath = resourcesDir+"/yellow_on_red4.png";
//        ArrayList<Color> colors = ImageTools.getImageColors(imgPath);
//        
//        assertEquals("red".toUpperCase(), ColorMapper.getColorName(colors.get(0)));
//        assertEquals("yellow".toUpperCase(), ColorMapper.getColorName(colors.get(1)));
//    }
//    
//    @Test
//    public void testGetTextPosition() {
//        try {
//            TextTools.getLines(resourcesDir+"/yellow_on_red4.png");
//        } catch (Exception ex) {
//            Logger.getLogger(ImageToolsTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
