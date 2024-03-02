package com.ohz.steps;

import com.ohz.common.BaseTestClass;
import com.ohz.common.Configuration;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import com.ohz.util.SolveWordleHelper;

public class Steps {

    @BeforeStep
    public void beforeStep(Scenario scenario){
        Configuration.setScenario(scenario);
    }

    @Given("user is on the Wordle page")
    public void launchWordle(){
        BaseTestClass baseTestClass = new BaseTestClass();
        baseTestClass.launchWordle();
    }

    @Given("let bot attempt to solve Wordle of the day starting with word {string}")
    public void letBotAttemptWordle(String startingWord){
        SolveWordleHelper solveWordleHelper = new SolveWordleHelper();
        solveWordleHelper.beatWordle(startingWord);
    }


}
