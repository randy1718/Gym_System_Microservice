Feature: Trainer Controller

######################################################
# CREATE TRAINER
######################################################

  Scenario: Create a trainer successfully
    Given a valid trainer registration request
    When the client creates the trainer
    Then the trainer response status should be 200
    And the generated trainer username should be "Mario.Hernandez"
    And the generated trainer password should not be empty

  Scenario: Create trainer without first name
    Given a trainer request without first name
    When the client creates the trainer
    Then the trainer response status should be 400

  Scenario: Create trainer without specialization
    Given a trainer request without specialization
    When the client creates the trainer
    Then the trainer response status should be 400

  Scenario: Create trainer with invalid specialization
    Given a trainer request with an invalid specialization
    When the client creates the trainer
    Then the trainer response status should be 400

######################################################
# UPDATE TRAINER
######################################################

  Scenario: Update a trainer successfully
    Given a valid trainer update request
    When the client updates the trainer
    Then the trainer response status should be 200
    And the updated trainer username should be "Alfredo.Cavalli"

  Scenario: Update trainer without username
    Given a trainer update request without username
    When the client updates the trainer
    Then the trainer response status should be 400

  Scenario: Update a non-existing trainer
    Given a trainer "Non.Existing.Trainer" does not exist
    When the client updates the trainer
    Then the trainer response status should be 404

  Scenario: Update trainer with invalid specialization
    Given a trainer update request with invalid specialization
    When the client updates the trainer
    Then the trainer response status should be 404

######################################################
# GET TRAINER
######################################################

  Scenario: Get trainer successfully
    Given a valid trainer profile request
    When the client gets the trainer
    Then the trainer response status should be 200
    And the trainer first name should be "Mary"
    And the trainer last name should be "Valetyn"
    And the trainer specialization should be "Cardio"

  Scenario: Get trainer without password
    Given a trainer profile request without password
    When the client gets the trainer
    Then the trainer response status should be 400

  Scenario: Get a non-existing trainer
    Given trainer profile for "Unknown.Trainer" does not exist
    When the client gets the trainer
    Then the trainer response status should be 404

  Scenario: Get trainer with invalid credentials
    Given a trainer profile request with invalid credentials
    When the client gets the trainer
    Then the trainer response status should be 400

######################################################
# ACTIVATE / DEACTIVATE TRAINER
######################################################

  Scenario: Activate or deactivate trainer successfully
    Given a valid trainer activation request
    When the client activates or deactivates the trainer
    Then the trainer response status should be 200

  Scenario: Activate or deactivate trainer without isActive
    Given a trainer activation request without isActive
    When the client activates or deactivates the trainer
    Then the trainer response status should be 400

  Scenario: Activate or deactivate a non-existing trainer
    Given trainer activation for "Unknown.User" does not exist
    When the client activates or deactivates the trainer
    Then the trainer response status should be 404

######################################################
# GET TRAINER TRAININGS
######################################################

  Scenario: Get trainer trainings successfully
    Given a valid trainer trainings request
    When the client gets the trainer trainings
    Then the trainer response status should be 200
    And the trainer should have 1 trainings
    And the first trainee name should be "Veronica Leon"

  Scenario: Get trainer trainings without username
    Given a trainer trainings request without username
    When the client gets the trainer trainings
    Then the trainer response status should be 400

  Scenario: Get trainer trainings with invalid date range
    Given a trainer trainings request with invalid date range
    When the client gets the trainer trainings
    Then the trainer response status should be 400

  Scenario: Get trainings for a non-existing trainer
    Given trainer trainings for "Unknown.Trainer" do not exist
    When the client gets the trainer trainings
    Then the trainer response status should be 404
