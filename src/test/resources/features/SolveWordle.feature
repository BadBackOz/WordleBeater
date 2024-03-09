@Regression @SolveWordle
Feature: Attempt to solve Wordle Feature

  #@excelFilePath=src/test/resources/excelTestDataFiles/solveWordleOfTheDay1.xlsx
   # @excelSheet=Sheet1
    #@excelKey=startingWithWord1
  Scenario Outline: Solve Wordle of the day starting with word '<startingWord>'
    Given user is on the Wordle page
    Then let bot attempt to solve Wordle of the day starting with word '<startingWord>'
    Examples:
      | startingWord |
      | HARES        |
      | QUEST        |
      | SCARE        |
      | SHEAR        |