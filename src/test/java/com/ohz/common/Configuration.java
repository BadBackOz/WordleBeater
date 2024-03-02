package com.ohz.common;

import com.ohz.util.CustomUtil;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    public static void setWebDriver(WebDriver driver){
        map.put("driver%s".formatted(Thread.currentThread().toString()), driver);
    }

    public static WebDriver getDriver(){
        return (WebDriver) map.get("driver%s".formatted(Thread.currentThread().toString()));
    }

    public static void removeWebDriver(){
        map.remove("driver%s".formatted(Thread.currentThread()));
    }

    public static void setScenario(Scenario scenario){
        map.put("scenario%s".formatted(Thread.currentThread().toString()), scenario);
    }

    public static Scenario getScenario(){
        return (Scenario) map.get("scenario%s".formatted(Thread.currentThread().toString()));
    }

    public static void removeScenario(){
        map.remove("scenario%s".formatted(Thread.currentThread().toString()));
    }

    public static void takeAndLogScreenshot(){
        CustomUtil.wait(400);
        byte[] bytes = ((ChromeDriver)Configuration.getDriver()).getScreenshotAs(OutputType.BYTES);
        String imageString = Base64.getEncoder().encodeToString(bytes);

        Configuration.getScenario().log(String.format("<button type='button' onClick=displayImage('data:image/png;base64,%s')>View Image</button>", imageString));
    }
}
