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
			ChromeOptions options = new ChromeOptions();
			//options.addArguments("window-size=1920,1080");
			options.addArguments("start-maximized");
			options.addArguments("disable-infobars");
			options.addArguments("--disable-extensions");

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			String scaleFactor = "1";
			if(screenSize.height < 1080){
				scaleFactor = "0.67";
			}

			options.addArguments("force-device-scale-factor=%s".formatted(scaleFactor));
			driver = new ChromeDriver(options);
			Configuration.setWebDriver(driver);
		}

		return driver;
	}

	public void launchWordle() {
		getDriver().get(BASE_URL);
		Configuration.logWithScreenshot("Launched: %s".formatted(BASE_URL));
	}

}
