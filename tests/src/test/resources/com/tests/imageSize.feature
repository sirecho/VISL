Feature: Image Size
    As a user 
    In order to test my web page
    I should be able to see the image of expected size on the web page

  Background:
    Given I am at the web page under test

  Scenario: See image of expected size
    When I open the home page
    Then I should see an image of expected size