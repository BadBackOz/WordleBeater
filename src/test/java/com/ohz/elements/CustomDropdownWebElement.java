package com.ohz.elements;

import com.ohz.common.Configuration;
import com.ohz.util.CustomUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.util.ArrayList;

public class CustomDropdownWebElement {

    private WebElement webElement;

    private String xpath = null;

    public CustomDropdownWebElement(WebElement webElement){
        this.webElement = webElement;
    }

    public CustomDropdownWebElement(String xpath){
        this.xpath = xpath;
    }

    public boolean waitForPresent(int timeoutInSeconds) {
        long endTime = System.currentTimeMillis() + timeoutInSeconds * 1000L;

        boolean isPresent = false;
        while (!isPresent && System.currentTimeMillis() < endTime) {
            try {
                if (!Configuration.getDriver().findElements(By.xpath(xpath)).isEmpty()) {
                    isPresent = true;
                    this.webElement = Configuration.getDriver().findElement(By.xpath(xpath));
                }
            } catch (Exception e) {
                System.out.printf("Waiting for presence of element: %s%n", xpath);
            }
            CustomUtil.wait(200);
        }

        if (!isPresent) {
            Configuration.getScenario().log("Element not present: %s".formatted(xpath));
            Assert.fail();
        }

        return isPresent;
    }

    public void selectOptionByVisibleText(String text, int timeoutInSeconds){
        this.waitForPresent(timeoutInSeconds);
        Select select = new Select(webElement);
        select.selectByVisibleText(text);
    }

    public void verifyOptions(ArrayList<String> expectedOptions, int timeoutInSeconds) {
        waitForPresent(timeoutInSeconds);
        Select select = new Select(webElement);

        ArrayList<String> actualOptions = new ArrayList<>();
        boolean isMatch = false;
        int i=0;
        while(i<3 && !isMatch){
            actualOptions = new ArrayList<>();
            for(WebElement option : select.getOptions()){
                actualOptions.add(option.getText());
            }

            if(actualOptions.equals(expectedOptions)){
                isMatch = true;
            }
            i = i+1;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        String message = "Expected Options: %s".formatted(expectedOptions) +" --- "
                + "Actual Options: %s".formatted(actualOptions);
        Configuration.getScenario().log(message);
        if(!isMatch){
            Assert.fail();
        }
    }
}
