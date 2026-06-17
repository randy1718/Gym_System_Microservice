Feature: Workload integration

  Scenario: Add training sends workload message
    Given a trainer exists in the database
    And a trainee exists in the database
    And a valid add test training request
    When the training is created
    Then the training should be stored in the database
    And a workload message should be published
    And the workload message should contain the trainer information

  Scenario: Boundary duration of 1 minute
    Given a trainer exists in the database
    And a trainee exists in the database
    And a training request with duration 1
    When the training is created
    Then a workload message should be published
    And the workload duration should be 1

  Scenario: Large duration is propagated to workload queue
    Given a trainer exists in the database
    And a trainee exists in the database
    And a training request with duration 1440
    When the training is created
    Then a workload message should be published
    And the workload duration should be 1440