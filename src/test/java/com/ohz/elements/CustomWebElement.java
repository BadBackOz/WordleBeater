package com.ohz.elements;

import com.ohz.common.Configuration;
import com.ohz.util.CustomUtil;
import org.openqa.selenium.*;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CustomWebElement {

    private WebElement webElement;

    private final String xpath;

    public CustomWebElement(WebElement webElement) {
        this.webElement = webElement;
        this.xpath = getXpath();
    }

    public CustomWebElement(String xpath) {
        this.xpath = xpath;
    }

    public boolean waitForPresent(int timeoutInSeconds, boolean failOnNotPresent) {
        long endTime = System.currentTimeMillis() + timeoutInSeconds * 1000L;

        boolean isPresent = false;
        while (!isPresent && System.currentTimeMillis() < endTime) {
            try {
                if (!Configuration.getDriver().findElements(By.xpath(xpath)).isEmpty()) {
                    isPresent = true;
                    webElement = Configuration.getDriver().findElement(By.xpath(xpath));
                }
            } catch (Exception e) {
                System.out.printf("Waiting for presence of element: %s%n", xpath);
            }
            CustomUtil.wait(200);
        }

        if (!isPresent && failOnNotPresent) {
            Configuration.getScenario().log("Element not present: %s".formatted(xpath));
            Assert.fail();
        }

        return isPresent;
    }

    public boolean isDisplayed(int timeoutInSeconds) {
        return waitForPresent(timeoutInSeconds, false);
    }

    public void click() {
        waitForPresent(10, true);
        webElement.click();
        Configuration.getScenario().log("Clicked on element: %s".formatted(xpath));
    }

    public void clickWithoutLogging() {
        waitForPresent(10, true);
        webElement.click();
    }

    public void javascriptClick() {
        waitForPresent(10, true);
        JavascriptExecutor executor = (JavascriptExecutor) Configuration.getDriver();
        executor.executeScript("arguments[0].click();", webElement);
        Configuration.getScenario().log("Clicked on element: %s".formatted(xpath));
    }

    public boolean isEnabled() {
        waitForPresent(10, false);
        try {
            return webElement.isEnabled();
        } catch (StaleElementReferenceException e) {
            System.out.println("StaleElementException: " + xpath + " --- " + e.getMessage());
            return false;
        }
    }

    public String getText() {
        waitForPresent(10, true);
        return webElement.getText();
    }

    public String getAttributeValue(String attribute) {
        waitForPresent(10, true);
        return webElement.getAttribute(attribute);
    }

    public CustomWebElement findElement(By by) {
        WebElement webElement = Configuration.getDriver().findElement(By.xpath(xpath));
        webElement = webElement.findElement(by);
        return new CustomWebElement(webElement);
    }

    public List<CustomWebElement> getListOfElement() {
        List<WebElement> webElementList = Configuration.getDriver().findElements(By.xpath(xpath));

        List<CustomWebElement> customWebElementList = new ArrayList<>();
        for (int i=0; i<webElementList.size(); i++) {
            customWebElementList.add(new CustomWebElement(String.format("("+xpath+")[%s]", i+1)));
        }

        return customWebElementList;
    }

    public void waitForAttributeNotValue(String attribute, String value, int timeoutInSeconds) {
        waitForPresent(5, true);
        boolean isMatch = true;
        long endTime = System.currentTimeMillis() + Duration.ofSeconds(timeoutInSeconds).getSeconds();

        String actualAttributeValue = null;
        while (isMatch && System.currentTimeMillis() < endTime) {
            try {
                CustomUtil.wait(200);
                actualAttributeValue = String.valueOf(webElement.getAttribute(attribute));
            } catch (Exception e) {
                System.out.printf("Exception: %s%n", e);
            }

            isMatch = value.equals(actualAttributeValue);
        }
    }

    public void verifyText(String expectedText) {
        waitForPresent(10, true);

        boolean isMatch = false;
        String actualText = null;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + 5000;

        while (!isMatch && System.currentTimeMillis() < endTime) {
            actualText = webElement.getAttribute("innerText");
            if(expectedText.equals(actualText)){
                isMatch = true;
            }
        }

        if (!isMatch) {
            String message = "Expected text: %s".formatted(expectedText) + " --- "
                    + "Actual Text: %s".formatted(actualText);

            Configuration.getScenario().log(message);
            Assert.fail(message);
        }
    }

    private String getXpath(){
        String[] xpathArray = webElement.toString().split("xpath: ");
        StringBuilder sb = new StringBuilder();
        for(String xpath : xpathArray){
            xpath = xpath.trim();
            sb.append(xpath.replaceAll("]$","").replaceAll("^\\[\\[.* ->","").replaceAll("\\.//","//").replaceAll("->","").replaceAll("]]]","]").trim());
        }
        return sb.toString();
    }
}