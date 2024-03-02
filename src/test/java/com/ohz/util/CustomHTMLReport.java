package com.ohz.util;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CustomHTMLReport {

    private static final String TAGS_EXECUTED = System.getProperty("cucumber.filter.tags").replace("@", "");
    private static final String ENVIRONMENT = System.getProperty("env").toUpperCase();

    private static final String PROJECT = System.getProperty("project");

    private static final String BODY_START = """
            <!DOCTYPE html>
            <html>
            <head>
            <script>
            function displayImage(base64URL){
                var win = window.open();
                win.document.write('<iframe src="' + base64URL  + '" frameborder="0" style="border:0; top:0px; left:0px; bottom:0px; right:0px; width:100%; height:100%;" allowfullscreen></iframe>');
            }
            </script>
            <style>
            h1   {color: #452226;}
            h2 {color: #4b615f;}
            table, tr, td, th {border: 1px solid; text-align: left;}
            th {background-color: #D3D3D3;}
            td {word-wrap:break-word;}
            </style>
            </head>
            """;

    private static final String OVERVIEW_TABLE = """
            <table>
            <tr>
            <th><strong>Environment</strong></th>
            <th><strong>Total Scenarios</strong></th>
            <th><strong>Passed</strong></th>
            <th><strong>Failed</strong></th>
            <th><strong>Cucumber Tags Executed</strong></th>
            </tr>
            <tr>
            <td>environmentPlaceholder</td>
            <td>totalScenariosPlaceholder</td>
            <td>passedPlaceholder</td>
            <td style='background-color:failedColorPlaceholder;'>failedPlaceholder</td>
            <td>cucumberTagsPlaceholder</td>
            </tr>
             </table>""";

    private static final String FEATURE_SECTION = "<h1>featurePlaceholder</h1>";

    private static final String SCENARIO_NAME_SECTION = "<h2>scenarioNamePlaceholder</h2>";

    private static final String SCENARIO_TABLE_HEADERS = """
            <table width='100%'>
              <tr>
                <th width='5%'>StepType</th>
                <th width='90%'>Step Description</th>
                <th width='5%'>Step Result</th>
              </tr>""";

    private static final String SCENARIO_TABLE_STEP_PASSED = """
            <tr style='background-color:#90EE90;'>
                <td width='5%'>stepTypePlaceholder</td>
                <td width='90%'>stepDescriptionPlaceholder</td>
                <td width='5%'>PASSED</td>
              </tr>""";

    private static final String SCENARIO_TABLE_STEP_FAILED = """
            <tr style='background-color:#FF0000;'>
                <td width='5%'>stepTypePlaceholder</td>
                <td width='90%'>stepDescriptionPlaceholder</td>
                <td width='5%'>FAILED</td>
              </tr>""";

    private static final String SCENARIO_TABLE_SUB_STEP_INFO = """
            <tr>
                <td width='5%'>Info</td>
                <td width='90%'>stepDescriptionPlaceholder</td>
                <td width='5%'></td>
              </tr>""";

    private static final String SCENARIO_TABLE_END = "</table>";

    private static final String BODY_END = "</body>";

    public static void generateHTMLReport() throws IOException {
        String testResultJson = FileUtils.readFileToString(new File("target/cucumber-reports/TestResult.json"), StandardCharsets.UTF_8);

        Gson gson = new Gson();
        Feature[] featureList = gson.fromJson(testResultJson, Feature[].class);

        StringBuilder htmlReportSB = new StringBuilder();
        htmlReportSB.append(BODY_START);
        htmlReportSB.append(mapOverviewTable(featureList));

        for (Feature feature : featureList) {
            String featureName = feature.getName();
            String featureSection = FEATURE_SECTION.replace("featurePlaceholder", featureName);
            htmlReportSB.append(featureSection);

            for (Element element : feature.getElements()) {
                htmlReportSB.append(SCENARIO_NAME_SECTION.replace("scenarioNamePlaceholder", element.getName()));
                htmlReportSB.append(SCENARIO_TABLE_HEADERS);
                for (Step step : element.getSteps()) {
                    String result = step.getResult().getStatus();
                    if ("Passed".equalsIgnoreCase(result)) {
                        String tableRowPassed = SCENARIO_TABLE_STEP_PASSED.replace("stepTypePlaceholder", step.getKeyword())
                                .replace("stepDescriptionPlaceholder", step.getName());
                        htmlReportSB.append(tableRowPassed);
                    } else if ("Failed".equalsIgnoreCase(result)) {
                        String tableRowFailed = SCENARIO_TABLE_STEP_FAILED.replace("stepTypePlaceholder", step.getKeyword())
                                .replace("stepDescriptionPlaceholder", step.getName());
                        htmlReportSB.append(tableRowFailed);
                    }

                    if (step.getOutput() != null) {
                        for (String output : step.getOutput()) {
                            String intoStep = SCENARIO_TABLE_SUB_STEP_INFO.replace("stepDescriptionPlaceholder", output);
                            htmlReportSB.append(intoStep);
                        }
                    }
                }
                htmlReportSB.append(SCENARIO_TABLE_END);
            }
        }
        htmlReportSB.append(BODY_END);

        LocalDateTime ldt = LocalDateTime.now();
        Path path = Paths.get("htmlreports\\%s_%s_%s_Y%sM%sD%s_H%sM%s.html".formatted(PROJECT, ENVIRONMENT, TAGS_EXECUTED, ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute()));
        Files.write(path, String.valueOf(htmlReportSB).getBytes());

        Files.setAttribute(path, "dos:readonly", true);
    }

    private static String mapOverviewTable(Feature[] featureList) {
        String overviewPlaceholder = OVERVIEW_TABLE;

        int totalScenarioCount = 0;
        int failedCount = 0;
        for (Feature feature : featureList) {
            totalScenarioCount = totalScenarioCount + feature.getElements().size();

            for (Element element : feature.getElements()) {
                for (Step step : element.getSteps()) {
                    if ("Failed".equalsIgnoreCase(step.getResult().getStatus())) {
                        failedCount = failedCount + 1;
                        break;
                    }
                }
            }
        }

        overviewPlaceholder = overviewPlaceholder.replace("environmentPlaceholder", ENVIRONMENT)
                .replace("totalScenariosPlaceholder", String.valueOf(totalScenarioCount))
                .replace("passedPlaceholder", String.valueOf(totalScenarioCount - failedCount))
                .replace("failedPlaceholder", String.valueOf(failedCount))
                .replace("cucumberTagsPlaceholder", TAGS_EXECUTED);

        if (failedCount > 0) {
            overviewPlaceholder = overviewPlaceholder.replace("failedColorPlaceholder", "#FF0000");
        } else {
            overviewPlaceholder = overviewPlaceholder.replace("failedColorPlaceholder", "#90EE90");
        }

        return overviewPlaceholder;
    }
}

class Feature {
    private ArrayList<Element> elements;
    private String name;

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

class Element {
    private String name;
    private ArrayList<Step> steps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }
}

class Step {
    private ArrayList<String> output;
    private Result result;
    private String name;
    private String keyword;

    public ArrayList<String> getOutput() {
        return output;
    }

    public void setOutput(ArrayList<String> output) {
        this.output = output;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

class Result {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

