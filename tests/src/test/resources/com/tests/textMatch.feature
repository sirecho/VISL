Feature: Text Matching
    As a user 
    In order to test my web page
    I should be able to see the exact expected text on the web page

  Background:
    Given I am at the web page under test

  Scenario: See exact expected text
    When I open the home page
    Then I should see the exact expected text