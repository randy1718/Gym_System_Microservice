Feature: Workload Microservice Integration Tests

#################################################
# MongoDB Integration
#################################################

  Scenario: Persist a new trainer
    Given trainer "john" does not exist in the DB
    When an ADD workload event of 60 minutes is received
    Then trainer "john" should exist in MongoDB
    And MongoDB should contain 60 minutes for trainer "john"

  Scenario: Update existing trainer
    Given trainer "john" already has 60 minutes in January 2026
    When an ADD workload event of 60 minutes is received
    Then MongoDB should contain 120 minutes for trainer "john"

#################################################
# Negative Integration Cases
#################################################

  Scenario: Delete workload for a non-existing trainer
    Given trainer "john" does not exist in the DB
    When a DELETE workload event of 60 minutes is received
    Then trainer "john" should not exist in MongoDB

  Scenario: Invalid action should not persist data
    Given trainer "john" does not exist in the DB
    When an INVALID workload event of 60 minutes is received
    Then an integration exception should be thrown
    And trainer "john" should not exist in MongoDB

#################################################
# JMS Integration
#################################################

  Scenario: JMS message updates trainer workload
    Given trainer "john" does not exist in the DB
    When a JMS workload message of 60 minutes is received
    Then trainer "john" should have 60 minutes in January 2026

  Scenario: JMS message updates an existing trainer
    Given trainer "john" already has 60 minutes in January 2026
    When a JMS workload message of 60 minutes is received
    Then trainer "john" should have 120 minutes in January 2026