package com.tests;
import com.visl.Element;
import com.visl.tools.TextTools;
import cucumber.api.java8.En;
import com.visl.tools.WebSession;
import cucumber.api.java.Before;
import java.io.IOException;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.opencv.core.Core;

public class StepDefinitions implements En {
    
    public static String URL = "file:///home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/www/index.html";
    private WebSession session;
    
    String webPath = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/www/";
    String imgPath = "/home/echo/NetBeansProjects/VISL/src/test/resources/org/visl/images/";
    String searocksImg = webPath+"searocks.png";
    String searocksMediumImg = imgPath+"searocks_medium.png";
    String owlImg = webPath+"owl.png";
    String searocksXPath = "//*[@id=\"content\"]/center/img";
    String contentXPath = "//*[@id=\"content\"]";    
    
    @Before
    public void setUp() throws IOException {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public StepDefinitions() throws IOException {
        
        Given("^I am at the web page under test$", () -> {
            try {
                session = new WebSession(URL);
            } catch (IOException ex) {
                Assert.fail("Failed to open web session for URL "+URL);
            }
            assertTrue(true);
        });

        When("^I open the home page$", () -> {
            assertTrue(true);
        });

        Then("^I should see and image of a cloud$", () -> {
            Element element = session.getElement(contentXPath);
            assertTrue(element.containsImage(searocksImg));
        });
        
        Then("^I should see an image at expected location$", () -> {
            Element element = session.getElementByLocation("Top Left");
            assertTrue(element.containsImage(owlImg));
        });
        
        Then("^I should see an image of expected size$", () -> {
            Element element = session.getElement(searocksXPath, 300, 200);
            assertTrue(element.hasExactImage(searocksImg));
        });
        
        Then("^I should see a resized version of the image$", () -> {
            Element element = session.getElement(searocksXPath);
            assertTrue(element.hasExactResizedImage(searocksMediumImg, 300, 200));
        });        
        
        Then("^I should see the exact expected text$", () -> {
            Element element = session.getElement(TestPage.P1_XPATH);
            String text = TestPage.P1_TEXT;
            assertTrue(element.hasExactText(text));
        });
        
        Then("^I should see the expected text$", () -> {
            Element element = session.getElement(TestPage.P1_XPATH);
            String text = TestPage.P1_TEXT.substring(133, 186);
            assertTrue(element.hasText(text));
        });
        
        Then("^I should see the text in expected color$", () -> {
            Element element = session.getElement(TestPage.P2_XPATH);
            assertTrue(element.hasTextColor("white"));
        });
        
        Then("^I should see the text in expected size$", () -> {
            Element element = session.getElement(TestPage.P1_XPATH);
            assertTrue(element.hasFontSize(TestPage.FONTSZ));
        });
        
        Then("^I should see the text with expected alignment$", () -> {
            Element element = session.getElement(TestPage.P1_XPATH);
            assertTrue(element.hasTextAligned(TextTools.Alignment.Left));
        });
        
        Then("^I should see the elements with expected alignment$", () -> {
            Element element1 = session.getElement(TestPage.P1_XPATH);
            Element element2 = session.getElement(TestPage.P2_XPATH);
            assertTrue(element1.isAligned(element2, TextTools.Alignment.Left));
        });
    }
}