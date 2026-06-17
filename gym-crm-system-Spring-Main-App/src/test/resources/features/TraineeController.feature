Feature: Trainee Controller

#################################################
# CREATION
#################################################

  Scenario: Successfully create a trainee
    Given a valid trainee registration request
    When the client creates the trainee
    Then the trainee response status should be 200
    And the generated username should be "Lucas.Diaz"
    And the generated password should not be empty

  Scenario: First name is missing
    Given a trainee request without first name
    When the client creates the trainee
    Then the trainee response status should be 400

  Scenario: Empty request body
    Given an empty trainee request
    When the client creates the trainee
    Then the trainee response status should be 400

  Scenario: Unexpected server error
    Given a valid trainee registration request
    And the trainee service is unavailable
    When the client creates the trainee
    Then the trainee response status should be 500

#################################################
# UPDATE
#################################################

  Scenario: Successfully update a trainee
    Given a valid trainee update request
    When the client updates the trainee
    Then the update should be successful

  Scenario: Update trainee without username
    Given a trainee update request without username
    When the client updates the trainee
    Then the trainee response status should be 400

  Scenario: Update trainee without first name
    Given a trainee update request without first name
    When the client updates the trainee
    Then the trainee response status should be 400

  Scenario: Update a non-existing trainee
    Given a trainee "Unknown.User" does not exist
    When the client updates the trainee
    Then the trainee response status should be 404

  Scenario: Update trainee with invalid date format
    Given a trainee update request with invalid date format
    When the client updates the trainee with raw json
    Then the trainee response status should be 400

#################################################
# GET TRAINEE
#################################################

  Scenario: Get an existing trainee
    Given a valid trainee profile request
    When the client requests the trainee profile
    Then the trainee response status should be 200
    And the trainee first name should be "Lucas"
    And the trainee last name should be "Diaz"
    And the trainee should be active

  Scenario: Get trainee without password
    Given a trainee profile request without password
    When the client requests the trainee profile
    Then the trainee response status should be 400

  Scenario: Get a non-existing trainee
    Given a requested trainee does not exist
    When the client requests the trainee profile
    Then the trainee response status should be 404

  Scenario: Get trainee with invalid credentials
    Given a trainee profile request with invalid credentials
    When the client requests the trainee profile
    Then the trainee response status should be 400

  Scenario: Get trainee without username
    Given a trainee profile request without username
    When the client requests the trainee profile
    Then the trainee response status should be 400

  Scenario: Get trainee with empty request
    Given an empty trainee profile request
    When the client requests the trainee profile
    Then the trainee response status should be 400

######################################################
# ACTIVATE / DEACTIVATE TRAINEE
######################################################

  Scenario: Deactivate an existing trainee
    Given a valid activate or deactivate trainee request
    When the client changes the trainee status
    Then the trainee response status should be 200

  Scenario: Activate or deactivate request without isActive
    Given an activate or deactivate request without active status
    When the client changes the trainee status
    Then the trainee response status should be 400

  Scenario: Activate or deactivate a non-existing trainee
    Given an activate or deactivate request for a non-existing trainee
    When the client changes the trainee status
    Then the trainee response status should be 404

######################################################
# DELETE TRAINEE
######################################################

  Scenario: Delete an existing trainee
    Given a valid trainee deletion request
    When the client deletes the trainee
    Then the trainee response status should be 200

  Scenario: Delete request without username
    Given a trainee deletion request without username
    When the client deletes the trainee
    Then the trainee response status should be 400

  Scenario: Delete a non-existing trainee
    Given a deletion request for a non-existing trainee
    When the client deletes the trainee
    Then the trainee response status should be 404

######################################################
# GET TRAINEE TRAININGS LIST
######################################################

  Scenario: Get trainee trainings successfully
    Given a valid trainee trainings list request
    When the client requests the trainee trainings list
    Then the trainee response status should be 200

  Scenario: Get trainee trainings without username
    Given a trainee trainings list request without username
    When the client requests the trainee trainings list
    Then the trainee response status should be 400

  Scenario: Get trainee trainings with invalid date range
    Given a trainee trainings list request with invalid date range
    When the client requests the trainee trainings list
    Then the trainee response status should be 400

  Scenario: Get trainings for a non-existing trainee
    Given a trainee trainings request for non existing trainee
    When the client requests the trainee trainings list
    Then the trainee response status should be 404

######################################################
# UPDATE TRAINEE TRAINERS LIST
######################################################

  Scenario: Update trainee trainers list successfully
    Given a valid trainee trainers list update request
    When the client updates the trainee trainers list
    Then the trainee response status should be 200

  Scenario: Update trainee trainers list without username
    Given a trainee trainers list update request without username
    When the client updates the trainee trainers list
    Then the trainee response status should be 400

  Scenario: Update trainers list for a non existing trainee
    Given a trainee trainers list request for a non existing trainee
    When the client updates the trainee trainers list
    Then the trainee response status should be 404

  Scenario: Update trainers list with a non existing trainer
    Given a trainee trainers list request with a non existing trainer
    When the client updates the trainee trainers list
    Then the trainee response status should be 404