package com.example.demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
public class VehicleScraper {

    public Map<String, String> getVehicleDetails(String first, String second) {
        Map<String, String> vehicleData = new HashMap<>();

        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Open in normal mode
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open website
            String url = "https://parivahan.gov.in/rcdlstatus/";
            driver.get(url);

            // Wait for page to load
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Find input fields using XPath (to handle dynamic IDs)
            WebElement regNo1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@id,'tf_reg_no1')]")));
            WebElement regNo2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@id,'tf_reg_no2')]")));
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'j_idt32')]")));

            // Enter registration number
            regNo1.sendKeys(first);
            regNo2.sendKeys(second);

            // Wait for user to manually enter CAPTCHA
            System.out.println("⚠️ Enter CAPTCHA in the browser, then press Enter in the terminal...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine(); // Wait for user input

            // Click submit button
            submitButton.click();
            Thread.sleep(5000); // Wait for response

            // Extract vehicle details from table
            List<WebElement> tableRows = driver.findElements(By.xpath("//table/tbody/tr"));
            for (WebElement row : tableRows) {
                List<WebElement> columns = row.findElements(By.tagName("td"));
                if (columns.size() == 2) {
                    String key = columns.get(0).getText().trim();
                    String value = columns.get(1).getText().trim();
                    vehicleData.put(key, value);
                }
            }
        } catch (Exception e) {
            vehicleData.put("error", "Failed to fetch data. " + e.getMessage());
        } finally {
            driver.quit(); // Close browser
        }

        return vehicleData;
    }
}
