Feature: Change user password

  Scenario: Successfully change password
    Given a user "Fabian.Gomez" exists
    And the old password is correct
    When the user changes the password to "dfslalc123_dvd"
    Then the user request should be successful

  Scenario: Old password is missing
    Given a user "Fabian.Gomez" exists
    When the old password is omitted
    Then the user response status should be 400

  Scenario: New password is missing
    Given a user "Fabian.Gomez" exists
    When the new password is omitted
    Then the user response status should be 400

  Scenario: Old password is incorrect
    Given a user "Fabian.Gomez" exists
    And the old password is invalid
    When the user requests a password change
    Then the user response status should be 400

  Scenario: User does not exist
    Given a user "Unknown.User" does not exist
    When the user requests a password change
    Then the user response status should be 404