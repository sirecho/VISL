Feature: Text Alignment
    As a user 
    In order to test my web page
    I should be able to see the text with expected alignment on the web page

  Background:
    Given I am at the web page under test

  Scenario: See text with expected alignment
    When I open the home page
    Then I should see the text with expected alignment