Tech Stack
 1. Java
 2. Selenium
 3. Cucumber
 4. Gherkin
 5. TestNG
 6. Gradle

Steps to Execute Tests
 1. Create Gradle Configuration.
 2. Set Run command to 'clean test -Denv="PROD" -Dparalleltestcount=3 -Dcucumber.filter.tags="@Regression"'.
 3. Run configuration.

Environment specific properties files
 1. Located within resources folder.
 2. Property file loaded is dependent on 'env' property in Gradle configuration.

Test Results Reports
 1. Cucumber HTML and JSON reports saved to 'target\cucumber-reports' after each execution.
 2. Custom HTML test report saved to 'htmlreports' folder after each execution. (Generated from Cucumber JSON file)
