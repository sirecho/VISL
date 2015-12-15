Feature: Image Present
    As a user 
    In order to test my web page
    I should be able to see an image at the expected location on the web page

  Background:
    Given I am at the web page under test

  Scenario: See image at expected location
    When I open the home page
    Then I should see an image at expected location