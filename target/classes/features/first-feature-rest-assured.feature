Feature: (e2e) Validate users

  @users
  Scenario: (e2e) Validate that the response of the user request is 200
    Given the following get request that brings us the users
    And the response is 200

  @usersIds
  Scenario Outline: (e2e) Validate that the response on page 2 contains the corresponding ids
    Given the following get request that brings us the users
    And the response is 200
    And the body response contains the corresponding ids
    Then the total page contains <page>

    Examples:
      | page |
      |  2   |

  @userPost
  Scenario Outline: (e2e) Validate post
    Given the following post request that add users
    And the response is 201 for the post
    And the body response contains key name
    Then the body response contains the "<name>" of the user created

    Examples:
      |   name   |
      |  ramlr93 |


  @userPut
  Scenario Outline: (e2e) Validate update create user
    Given the following put request that update users
    And the response is 200 for the put
    Then the body response contains update "<updated_name>"
    Examples:
      | updated_name |
      | ramlr93Modif |

  @userDelete
  Scenario: (e2e) Validate delete user
    Given the following post request that add users
    And the following delete request that delete user
    And the response is 204 for the delete
    Then the body response is empty