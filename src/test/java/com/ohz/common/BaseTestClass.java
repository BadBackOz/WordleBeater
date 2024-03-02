package com.ohz.common;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;

public class BaseTestClass {

	private static final String BASE_URL = System.getProperty("env.url");

	public WebDriver getDriver() {
		WebDriver driver = Configuration.getDriver();

		if (driver == null) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			String scaleFactor = "1";
			if(screenSize.height < 1080){
				scaleFactor = "0.67";
			}

			ChromeOptions options = new ChromeOptions();
			options.addArguments("force-device-scale-factor=%s".formatted(scaleFactor));
			driver = new ChromeDriver(options);
			Configuration.setWebDriver(driver);
		}

		return driver;
	}

	public void launchWordle() {
		getDriver().get(BASE_URL);
		getDriver().manage().window().maximize();
		Configuration.getScenario().log("Launched: %s".formatted(BASE_URL));
	}

}
