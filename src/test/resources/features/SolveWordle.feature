@Regression @SolveWordle
Feature: Attempt to solve Wordle Feature

  Scenario Outline: Solve Wordle of the day starting with word '<startingWord>'
    Given user is on the Wordle page
    Then let bot attempt to solve Wordle of the day starting with word '<startingWord>'
    Examples:
      | startingWord |
      | HARES        |
      | STARE        |
      | NULL         |
      | LIMPS        |
      | POXSE        |