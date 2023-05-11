package com.koli;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TestApps {
    private WebDriver driver;
    private FileWriter csvWriter;

    @BeforeClass
    public void setUp() {
        // Set up the WebDriver instance
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        // Set up the CSV writer
        try {
            csvWriter = new FileWriter(new File("test_results.csv"));
            csvWriter.append("Link Text,Expected URL,Actual URL,Result\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        // Clean up the WebDriver instance
        driver.quit();

        // Clean up the CSV writer
        try {
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testLink1() {
        // Set up the test data
        String linkText = "Student Services Centre";
        String expectedUrl = "https://www.ualberta.ca/services/student-service-centre/index.html";
        String actualUrl = "";

        // Navigate to the initial page
        driver.get("https://apps.ualberta.ca/");

        // Find the link with the specified text
        WebElement link = driver.findElement(By.linkText(linkText));

        // Get the actual URL of the link and click it
//        actualUrl = link.getAttribute("href");
        link.click();

        // Wait for the page to become stable
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("navSearchButton")));

        // Get the actual URL of the page and compare it with the expected URL
        String actualPageUrl = driver.getCurrentUrl();
        String result = actualPageUrl.equals(expectedUrl) ? "Pass" : "Fail";

        System.out.println("result : " + result);

        // Write the test results to the CSV file
        try {
            csvWriter.append(String.format("%s,%s,%s,%s\n", linkText, expectedUrl, actualPageUrl, result));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ...
        // ...
    }

}
