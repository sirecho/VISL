Feature: Text Present
    As a user 
    In order to test my web page
    I should be able to see the expected text on the web page

  Background:
    Given I am at the web page under test

  Scenario: See expected text
    When I open the home page
    Then I should see the expected text