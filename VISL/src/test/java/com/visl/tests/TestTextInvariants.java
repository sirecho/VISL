/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl.tests;

import com.visl.Constants;
import com.visl.Element;
import com.visl.TestPage;
import com.visl.tools.TextTools;
import com.visl.tools.WebSession;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opencv.core.Core;

/**
 *
 * @author echo
 */
public class TestTextInvariants {
    
    private static final Logger log = Logger.getLogger(TestTextInvariants.class.getName());
    
    private static WebSession session;
    
    private static final String NOMATCH = "This text is not matching.";
    private static final String NOMATCH_LONG = "This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching. This text is not matching.";
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        log.log(Level.INFO, "Setting up a new session");
        session = new WebSession(TestPage.URL);        
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
     * Test with exact matching text.
     */
    @Test
    public void matching_exact() {
        System.out.println("Test with exact matching text");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = TestPage.P1_TEXT;
        assertTrue(element.hasExactText(text));
    }
    
    /**
     * Test with nonmatching text.
     */
    @Test
    public void nonmatching_exact() {
        System.out.println("Test with nonmatching text");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = NOMATCH;
        assertFalse(element.hasExactText(text));
    }

    /**
     * Test with nonmatching text that is longer.
     */
    @Test
    public void nonmatching_longer() {
        System.out.println("Test with nonmatching text that is longer");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = NOMATCH_LONG;
        assertFalse(element.hasExactText(text));
    }
    
    /**
     * Test with substring that matches the beginning.
     */
    @Test
    public void matching_sub_beginnig() {
        System.out.println("Test with substring that matches the beginning");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = TestPage.P1_TEXT.substring(0, 132);
        assertTrue(element.hasText(text));
    }
    
    /**
     * Test with substring that matches the middle.
     */
    @Test
    public void matching_sub_middle() {
        System.out.println("Test with substring that matches the middle");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = TestPage.P1_TEXT.substring(133, 186);
        assertTrue(element.hasText(text));
    }
    
    /**
     * Test with substring that matches the end.
     */
    @Test
    public void matching_sub_end() {
        System.out.println("Test with substring that matches the end");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = TestPage.P1_TEXT.substring(415, TestPage.P1_TEXT.length());
        assertTrue(element.hasText(text));
    }
    
    /**
     * Test with substring that does not match.
     */
    @Test
    public void nonmatching_sub() {
        System.out.println("Test with substring that does not match");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = NOMATCH;
        assertFalse(element.hasText(text));
    }    
    
    /**
     * Test with substring that does not match and is longer.
     */
    @Test
    public void nonmatching_sub_longer() {
        System.out.println("Test with substring that does not match and is longer");
        Element element = session.getElement(TestPage.P1_XPATH);
        String text = NOMATCH_LONG;
        assertFalse(element.hasText(text));
    }
    
    /**
     * Test with matching color.
     */
    @Test
    public void matching_color() throws FileNotFoundException {
        System.out.println("Test with matching color");
        Element element = session.getElement(TestPage.P2_XPATH);
        assertTrue(element.hasTextColor("white"));
    }
    
    /**
     * Test with nonmatching color.
     */
    @Test
    public void nonmatching_color() throws FileNotFoundException {
        System.out.println("Test with nonmatching color");
        Element element = session.getElement(TestPage.P2_XPATH);
        assertFalse(element.hasTextColor("blue"));
    }
    
    /**
     * Test with matching font size.
     */
    @Test
    public void matching_fontsize() {
        System.out.println("Test with matching font size");
        Element element = session.getElement(TestPage.P1_XPATH);
        assertTrue(element.hasFontSize(TestPage.FONTSZ));
    }

    /**
     * Test with nonmatching font size.
     */
    @Test
    public void nonmatching_fontsize() {
        System.out.println("Test with nonmatching font size");
        Element element = session.getElement(TestPage.P1_XPATH);
        assertFalse(element.hasFontSize(0));
    }
    
    /**
     * Test matching left alignment.
     */
    @Test
    public void matching_alignment_left() {
        System.out.println("Test matching left alignment");
        Element element = session.getElement(TestPage.P1_XPATH);
        assertTrue(element.hasTextAligned(TextTools.Alignment.Left));
    }
    
    /**
     * Test nonmatching left alignment.
     */
    @Test
    public void nonmatching_alignment_left() {
        System.out.println("Test nonmatching left alignment");
        Element element = session.getElement(TestPage.P1_XPATH);
        assertFalse(element.hasTextAligned(TextTools.Alignment.Right));
    }    
    
    /**
     * Test matching right alignment.
     */
    @Test
    public void matching_alignment_right() {
        System.out.println("Test matching right alignment");
        Element element = session.getElement(TestPage.P3_XPATH);
        assertTrue(element.hasTextAligned(TextTools.Alignment.Right));
    }
    
    /**
     * Test nonmatching right alignment.
     */
    @Test
    public void nonmatching_alignment_right() {
        System.out.println("Test nonmatching right alignment");
        Element element = session.getElement(TestPage.P3_XPATH);
        assertFalse(element.hasTextAligned(TextTools.Alignment.Center));
    }    

    /**
     * Test matching center alignment.
     */
    @Test
    public void matching_alignment_center() {
        System.out.println("Test matching center alignment");
        Element element = session.getElement(TestPage.P4_XPATH);
        assertTrue(element.hasTextAligned(TextTools.Alignment.Center));
    }
    
    /**
     * Test nonmatching center alignment.
     */
    @Test
    public void nonmatching_alignment_center() {
        System.out.println("Test nonmatching center alignment");
        Element element = session.getElement(TestPage.P4_XPATH);
        assertFalse(element.hasTextAligned(TextTools.Alignment.Left));
    }    
    
    /**
     * Test matching justified alignment.
     */
    @Test
    public void matching_alignment_justified() {
        System.out.println("Test matching justified alignment");
        Element element = session.getElement(TestPage.P2_XPATH);
        assertTrue(element.hasTextAligned(TextTools.Alignment.Justified));
    }

    /**
     * Test nonmatching justified alignment.
     */
    @Test
    public void nonmatching_alignment_justified() {
        System.out.println("Test nonmatching justified alignment");
        Element element = session.getElement(TestPage.P2_XPATH);
        assertTrue(element.hasTextAligned(TextTools.Alignment.Left));
    }    
    
    /**
     * Test matching left alignment for two elements.
     */
    @Test
    public void matching_elements_alignment_left() {
        System.out.println("Test matching left alignment for two elements");
        Element element1 = session.getElement(TestPage.P1_XPATH);
        Element element2 = session.getElement(TestPage.P2_XPATH);
        assertTrue(element1.isAligned(element2, TextTools.Alignment.Left));
    }
    
    /**
     * Test nonmatching alignment for two elements.
     */
    @Test
    public void nonmatching_elements_alignment() {
        System.out.println("Test nonmatching alignment for two elements");
        Element element1 = session.getElement(TestPage.P1_XPATH);
        Element element2 = session.getElement(TestPage.P3_XPATH);
        assertFalse(element1.isAligned(element2, TextTools.Alignment.Left));
    }
}
