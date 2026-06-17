Feature: Training Controller

######################################################
# ADD TRAINING
######################################################

Scenario: Add training successfully
Given a valid add training request
When the client adds the training
Then the training response status should be 200

Scenario: Add training without trainee username
Given an add training request without trainee username
When the client adds the training
Then the training response status should be 400

Scenario: Add training with invalid duration
Given an add training request with invalid duration
When the client adds the training
Then the training response status should be 400

Scenario: Add training for a non existing trainee
Given an add training request for a non existing trainee
When the client adds the training
Then the training response status should be 404