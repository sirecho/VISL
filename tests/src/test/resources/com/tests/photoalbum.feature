Feature: View Photo Album
    As a user 
    In order to see my photos
    I should be able to navigate the photo album and see individual photos

  Background:
    Given I am at the photo album page

  Scenario: Open photo
    When I click my first photo
    Then I should see my first photo