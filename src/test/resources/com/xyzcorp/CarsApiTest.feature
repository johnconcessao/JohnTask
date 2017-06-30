Feature:
  As a customer, I want to select the type of car I want to sell, so that I can receive an offer from automobiles.com.

  Background:
    Given non-empty cars manufacturers list is grabbed

  Scenario:
    When select one random manufacturer
    And choose one of manufacturer models
    Then at least one manufacturing model year is available
    And selected car is set

  Scenario:
    When select one random manufacturer
    And set non-existing manufacturer model
    Then empty entries returned


  Scenario:
    Then each model have its years of manufacturing