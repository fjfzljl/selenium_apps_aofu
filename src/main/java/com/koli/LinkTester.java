package com.koli;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LinkTester {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("https://apps.ualberta.ca/");

        try {

            List<LinkValidationResult> results = new ArrayList<>();

            Map<String, String> checkLinkTextExpectUrlOldPage = new HashMap<>();


            // need login to get auth
            checkLinkTextExpectUrlOldPage.put("Login", "https://login.ualberta.ca/module.php/core/loginuserpass.php?AuthState=");

            checkLinkTextExpectUrlOldPage.put("Course Catalogue", "https://login.ualberta.ca/module.php/core/loginuserpass.php?AuthState=");

            checkLinkTextExpectUrlOldPage.put("Student Services Centre", "https://www.ualberta.ca/services/student-service-centre/index.html");
            checkLinkTextExpectUrlOldPage.put("Staff Services Centre", "https://www.ualberta.ca/services/staff-service-centre/index.html");


            for (Map.Entry<String, String> entry : checkLinkTextExpectUrlOldPage.entrySet()) {
                String checkLinkText = entry.getKey();
                String expectedUrl = entry.getValue();
                results.add(validateLinkInOldPage(driver, checkLinkText, expectedUrl));
//                results.add(validateLinkInOldPage(checkLinkText, expectedUrl));
            }

            driver.quit();

            Map<String, String> checkLinkTextExpectUrlNewPage = new HashMap<>();

            // need login to get auth
            checkLinkTextExpectUrlNewPage.put("eClass", "login.ualberta.ca");
            checkLinkTextExpectUrlNewPage.put("University Gmail", "login.ualberta.ca");

            checkLinkTextExpectUrlNewPage.put("Bear Tracks", "https://www.beartracks.ualberta.ca");
            checkLinkTextExpectUrlNewPage.put("BearsDen", "https://alberta.campuslabs.ca/engage");
            checkLinkTextExpectUrlNewPage.put("Library", "https://library.ualberta.ca");


            for (Map.Entry<String, String> entry : checkLinkTextExpectUrlNewPage.entrySet()) {
                String checkLinkText = entry.getKey();
                String expectedUrl = entry.getValue();
//                results.add(validateLinkInNewPage(driver, checkLinkText, expectedUrl));
                results.add(validateLinkInNewPage(checkLinkText, expectedUrl));
            }

            writeResultsToCsv(results);

            // close the browser
        } catch (Exception e){
            e.printStackTrace();
        }
        driver.quit();
    }

//    private static LinkValidationResult validateLinkInNewPage(WebDriver driver, String linkText, String expectedUrl) {
    private static LinkValidationResult validateLinkInNewPage(String linkText, String expectedUrl) {

        WebDriver driver = new ChromeDriver();
        driver.get("https://apps.ualberta.ca/");

        WebElement link = driver.findElement(By.linkText(linkText));

        link.click();

        // wait for a new window or tab to open
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // switch to the new window or tab
        String originalWindowHandle = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        wait.until(ExpectedConditions.urlContains(expectedUrl));

        String actualUrl = driver.getCurrentUrl();

        LinkValidationResult result = new LinkValidationResult(linkText, expectedUrl, actualUrl);

//        driver.close();

        driver.switchTo().window(originalWindowHandle);

        driver.quit();

        return result;
    }

    private static LinkValidationResult validateLinkInOldPage(WebDriver driver, String linkText, String expectedUrl) {

        driver.get("https://apps.ualberta.ca/");
        WebElement link = driver.findElement(By.linkText(linkText));

        link.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.urlContains(expectedUrl));

        String actualUrl = driver.getCurrentUrl();

        LinkValidationResult result = new LinkValidationResult(linkText, expectedUrl, actualUrl);

        return result;
    }

    private static void writeResultsToCsv(List<LinkValidationResult> results) {
        String csvFile = "link_validation_results.csv";
        String csvHeader = "Link Text,Expected URL,Actual URL,Result\n";

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append(csvHeader);

            for (LinkValidationResult result : results) {
                String csvRow = result.getLinkText() + "," + result.getExpectedUrl() + ","
                        + result.getActualUrl() + "," + result.getResult() + "\n";
                writer.append(csvRow);
            }

            System.out.println("Link validation results written to " + csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class LinkValidationResult {
    private final String linkText;
    private final String expectedUrl;
    private final String actualUrl;
    private final String result;

    public LinkValidationResult(String linkText, String expectedUrl, String actualUrl) {
        this.linkText = linkText;
        this.expectedUrl = expectedUrl;
        this.actualUrl = actualUrl;
        this.result = actualUrl.contains(expectedUrl) ? "PASS" : "FAIL";
    }

    public String getLinkText() {
        return linkText;
    }

    public String getExpectedUrl() {
        return expectedUrl;
    }

    public String getActualUrl() {
        return actualUrl;
    }

    public String getResult() {
        return result;
    }
}
