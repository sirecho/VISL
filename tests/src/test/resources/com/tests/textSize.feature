Feature: Text Size
    As a user 
    In order to test my web page
    I should be able to see the text in expected size on the web page

  Background:
    Given I am at the web page under test

  Scenario: See text in expected size
    When I open the home page
    Then I should see the text in expected size