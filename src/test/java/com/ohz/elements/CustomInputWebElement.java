package com.ohz.elements;

import com.ohz.common.Configuration;
import com.ohz.util.CustomUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class CustomInputWebElement {

    private WebElement webElement;

    private String xpath = null;

    public CustomInputWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public CustomInputWebElement(String xpath) {
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

    public void type(String text, int timeoutInSeconds) {
        waitForPresent(timeoutInSeconds);
        webElement.clear();
        webElement.sendKeys(text);
    }

}
