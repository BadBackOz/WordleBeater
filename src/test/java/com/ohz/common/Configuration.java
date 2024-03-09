package com.ohz.common;

import io.cucumber.java.Scenario;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    public static void setWebDriver(WebDriver driver) {
        map.put("driver%s".formatted(Thread.currentThread().toString()), driver);
    }

    public static WebDriver getDriver() {
        return (WebDriver) map.get("driver%s".formatted(Thread.currentThread().toString()));
    }

    public static void removeWebDriver() {
        map.remove("driver%s".formatted(Thread.currentThread()));
    }

    public static void setScenario(Scenario scenario) {
        map.put("scenario%s".formatted(Thread.currentThread().toString()), scenario);
    }

    public static Scenario getScenario() {
        return (Scenario) map.get("scenario%s".formatted(Thread.currentThread().toString()));
    }

    public static void removeScenario() {
        map.remove("scenario%s".formatted(Thread.currentThread().toString()));
    }

    public static void removeExcelData() {
        map.remove("excelData%s".formatted(Thread.currentThread().toString()));
    }

    public static void logWithScreenshot(String textToLog) {

        byte[] bytes = ((ChromeDriver) Configuration.getDriver()).getScreenshotAs(OutputType.BYTES);
        String imageString = Base64.getEncoder().encodeToString(bytes);

        String screenshotToLog = String.format("<button style='background-color:blue; color:white;' type='button' onClick=displayImage('data:image/png;base64,%s')>View Image</button>", imageString);

        getScenario().log(String.format("%s --- %s", textToLog, screenshotToLog));
    }

    public static void takeAndLogScreenshot() {
        byte[] bytes = ((ChromeDriver) Configuration.getDriver()).getScreenshotAs(OutputType.BYTES);
        String imageString = Base64.getEncoder().encodeToString(bytes);

        getScenario().log(String.format("<button style='background-color:blue; color:white;' type='button' onClick=displayImage('data:image/png;base64,%s')>View Image</button>", imageString));
    }

    public static HashMap<String, String> getExcelData() {
        HashMap<String, String> testData = (HashMap<String, String>) map.get("excelData%s".formatted(Thread.currentThread().toString()));

        String filePath = null;
        String sheetName = null;
        String key = null;
        if (null == testData) {
            for (String tag : getScenario().getSourceTagNames()) {
                if (tag.startsWith("@excelFilePath")) {
                    String[] split = tag.split("=");
                    filePath = split[1];
                } else if (tag.startsWith("@excelSheet")) {
                    String[] split = tag.split("=");
                    sheetName = split[1];
                } else if (tag.startsWith("@excelKey")) {
                    String[] split = tag.split("=");
                    key = split[1];
                }
            }
        }

        return getDataFromExcelFile(filePath, sheetName, key);
    }

    private static HashMap<String, String> getDataFromExcelFile(String filePath, String sheetName, String key) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        HashMap<String, String> testData = new HashMap<>();
        if (null != filePath && null != sheetName && null != key) {
            try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
                XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
                int rowCount = workBook.getSheet(sheetName).getLastRowNum();
                for (int i = 0; i < rowCount; i++) {
                    String cellValue;
                    try{
                        cellValue = workBook.getSheet(sheetName).getRow(i).getCell(0).getStringCellValue();
                    }catch(Exception e){
                        continue;
                    }

                    if (cellValue.equals(key)) {
                        int columnCount = workBook.getSheet(sheetName).getRow(i).getLastCellNum();
                        for (int j = 1; j < columnCount; j++) {
                            String columnName = workBook.getSheet(sheetName).getRow(i).getCell(j).getStringCellValue();
                            if(key.equals(columnName)){
                                break;
                            }
                            String columnValue = workBook.getSheet(sheetName).getRow(i+1).getCell(j).getStringCellValue();
                            testData.put(columnName, columnValue);
                        }
                        return testData;
                    }
                }
            } catch (IOException e) {
                Configuration.getScenario().log("Exception: %s".formatted(e));
                throw new RuntimeException(e);
            }
        }
        return testData;
    }
}
