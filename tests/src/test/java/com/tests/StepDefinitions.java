package com.tests;
import cucumber.api.PendingException;
import cucumber.api.java8.En;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.visl.Page;
import cucumber.api.java8.StepdefBody;
import static org.junit.Assert.assertTrue;

public class StepDefinitions implements En {
    
    public StepDefinitions() {
        Given("^I am at the photo album page$", () -> {
            // TODO: Write code to navigate to the page
            FirefoxDriver driver = new FirefoxDriver();
        });

        When("^I click my first photo$", () -> {
            assertTrue(true);
        });

        Then("^I should see my first photo$", () -> {
            Page myPage = new Page();
            assertTrue(false);
        });
    }
}
