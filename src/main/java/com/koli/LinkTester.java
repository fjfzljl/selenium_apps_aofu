package com.koli;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LinkTester {
    public static void main(String[] args) {
        String checkLinkText;
        String expectedUrl;
        String expectedPageCheckBody;

        WebDriver driver = new ChromeDriver();

        try {

            List<LinkValidationResult> results = new ArrayList<>();

            checkLinkText = "Login";
            expectedUrl = "https://login.ualberta.ca/module.php/core/loginuserpass.php?AuthState=";
            expectedPageCheckBody = "//input[@id='username']";
            results.add(validateLinkInOldPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            checkLinkText = "Course Catalogue";
            expectedUrl = "https://login.ualberta.ca/module.php/core/loginuserpass.php?AuthState=";
            expectedPageCheckBody = "//input[@id='username']";
            results.add(validateLinkInOldPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            checkLinkText = "Student Services Centre";
            expectedUrl = "https://www.ualberta.ca/services/student-service-centre/index.html";
            expectedPageCheckBody = "//h1[normalize-space()='Student Service Centre']";
            results.add(validateLinkInOldPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            checkLinkText = "Staff Services Centre";
            expectedUrl = "https://www.ualberta.ca/services/staff-service-centre/index.html";
            expectedPageCheckBody = "//h1[normalize-space()='Staff Service Centre']";
            results.add(validateLinkInOldPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            checkLinkText = "Information Services and Technology";
            expectedUrl = "https://uofaprod.service-now.com/sp?id=index";
            expectedPageCheckBody = "(//input[@placeholder='Search'])[1]";
            results.add(validateLinkInOldPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            // need login to get auth
            checkLinkText = "eClass";
            expectedUrl = "https://login.ualberta.ca/module.php/core/loginuserpass.php?AuthState=";
            expectedPageCheckBody = "//input[@id='username']";
            results.add(validateLinkInNewPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            // need login to get auth
            checkLinkText = "University Gmail";
            expectedUrl = "https://login.ualberta.ca/module.php/core/loginuserpass.php?AuthState=";
            expectedPageCheckBody = "//input[@id='username']";
            results.add(validateLinkInNewPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));


            checkLinkText = "Bear Tracks";
            expectedUrl = "https://www.beartracks.ualberta.ca";
            expectedPageCheckBody = "//a[@title='Bear Tracks']";
            results.add(validateLinkInNewPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            checkLinkText = "Library";
            expectedUrl = "https://library.ualberta.ca";
            expectedPageCheckBody = "//a[normalize-space()='Find a Person']";
            results.add(validateLinkInNewPage(driver, checkLinkText, expectedUrl, expectedPageCheckBody));

            writeResultsToCsv(results);

        } catch (Exception e) {
            e.printStackTrace();
        }

        driver.close();

    }

    private static LinkValidationResult validateLinkInNewPage(WebDriver driver, String linkText, String expectedUrl, String expectedPageCheckBody) {

        driver.get("https://apps.ualberta.ca/");

        WebElement link = driver.findElement(By.linkText(linkText));

        link.click();

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

//        wait.until(ExpectedConditions.urlContains(expectedUrl));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(expectedPageCheckBody)));

        String actualUrl = driver.getCurrentUrl();

        LinkValidationResult result = new LinkValidationResult(linkText, expectedUrl, actualUrl);

        driver.close();

        driver.switchTo().window(originalWindowHandle);

        return result;
    }

    private static LinkValidationResult validateLinkInOldPage(WebDriver driver, String linkText, String expectedUrl, String expectedPageCheckBody) {

        driver.get("https://apps.ualberta.ca/");
        WebElement link = driver.findElement(By.linkText(linkText));

        link.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(expectedPageCheckBody)));

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
