@FrontEnd @Orders
Feature: Validate ordering

  Background: Open browser and navigate to login page
    Given User is on login page
    When The user enter a valid credentials
    And The user clicks on login button

  @Skip
  Scenario: Place item in the shopping cart
    And clicks button to add item in cart
    Then should be visible counter red badge on cart icon

  Scenario: Place multiple items in the shopping cart
    And In inventory page select 3 items
    And User clicks on shopping cart icon
    Then There should be 4 items in the shopping cart list

  Scenario Outline: Validate tax and total
    And User selects
      | Sauce Labs Backpack               |
      | Sauce Labs Bike Light             |
      | Sauce Labs Bolt T-Shirt           |
      | Sauce Labs Fleece Jacket          |
      | Sauce Labs Onesie                 |
      | Test.allTheThings() T-Shirt (Red) |

    And User clicks on shopping cart icon
    And Clicks Checkout button
    And User enter "<firstName>", "<lastName>" and "<zipCode>"
    And Clicks Continue button
    Then Tax should be "Tax: $10.40"
    And Total should be "Total: $140.34"
    Examples:
      | firstName | lastName | zipCode |
      | Nitin     | Patil    | 63368   |