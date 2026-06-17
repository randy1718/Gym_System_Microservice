Feature: Workload Microservice Component Tests

#################################################
# Positive Cases
#################################################

  Scenario: Add workload for a new trainer
    Given trainer "john" does not exist
    When an ADD workload event of 60 minutes is processed
    Then the trainer should have 60 minutes for January 2026
    And the repository should save the trainer

  Scenario: Accumulate workload
    Given trainer "john" already has 60 minutes
    When an ADD workload event of 60 minutes is processed
    Then the trainer should have 120 minutes for January 2026
    And the repository should update the trainer

  Scenario: Delete workload
    Given trainer "john" already has 60 minutes
    When a DELETE workload event of 60 minutes is processed
    Then the trainer should not have workload for January 2026

#################################################
# Negative Cases
#################################################

  Scenario: Invalid action type
    Given trainer "john" does not exist
    When an INVALID workload event of 60 minutes is processed
    Then an exception should be thrown

#################################################
# Boundary Cases
#################################################

  Scenario: Add zero workload
    Given trainer "john" does not exist
    When an ADD workload event of 0 minutes is processed
    Then the trainer should have 0 minutes for January 2026

  Scenario: Delete more workload than exists
    Given trainer "john" already has 60 minutes
    When a DELETE workload event of 120 minutes is processed
    Then the trainer should not have workload for January 2026

  Scenario: Add workload for another month
    Given trainer "john" already has 60 minutes
    When an ADD workload event of 90 minutes is processed for February 2026
    Then the trainer should have 90 minutes for February 2026

  Scenario: Add workload for another year
    Given trainer "john" already has 60 minutes
    When an ADD workload event of 50 minutes is processed for January 2027
    Then the trainer should have 50 minutes for January 2027