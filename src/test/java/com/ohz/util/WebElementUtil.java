package com.ohz.util;

import org.openqa.selenium.WebElement;

public class WebElementUtil {
	

	/**
	 * 
	 * @param attributeName
	 * @param valueToNotContain
	 * @param timeoutInSeconds
	 * @return value of attribute
	 */
	public static String waitForAttributeToNotContain(WebElement webElement, String attributeName, String valueToNotContain, int timeoutInSeconds) {
		
		long startTime = System.currentTimeMillis();
		long timeToWait = 1000 * timeoutInSeconds;
		long endTime = startTime + timeToWait;
		
		boolean isValueMatch = false;
		String value = null;
		
		while(System.currentTimeMillis() < endTime && isValueMatch==false) {
			value = webElement.getAttribute(attributeName);
			if(!value.contains(valueToNotContain) && (value.contains("absent") || value.contains("present") || value.contains("correct"))) {
				return value;
			}
		}
		
		return value;
	}

	public static boolean waitForPresent(WebElement webElement, int timeoutInSeconds){
		long startTime = System.currentTimeMillis();
		long timeToWait = 1000L * timeoutInSeconds;
		long endTime = startTime + timeToWait;

		boolean isPresent = false;
		boolean isEnabled = false;

		while(System.currentTimeMillis() < endTime && !isPresent && !isEnabled){
			try{
				isPresent = webElement.isDisplayed();
				isEnabled = webElement.isEnabled();
			}catch (Exception e){
				System.out.println("Element not loaded yet: " + webElement);
			}
		}

		return isEnabled;
	}

}
