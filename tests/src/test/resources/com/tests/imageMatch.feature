Feature: Image Matching
    As a user 
    In order to test my web page
    I should be able to see the expected image on the web page

  Background:
    Given I am at the web page under test

  Scenario: See expected image
    When I open the home page
    Then I should see and image of a cloud