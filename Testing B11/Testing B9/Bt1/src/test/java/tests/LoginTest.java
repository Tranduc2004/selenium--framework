// src/test/java/tests/LoginTest.java
package tests;

import framework.config.CredentialManager;
import framework.factory.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginTest {
    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");
        driver = DriverFactory.createDriver(browser);
        driver.get("https://www.saucedemo.com");
    }

    @Test
    public void testLoginSuccess() {
        // Đọc credential từ Secret hoặc config file
        driver.findElement(By.id("user-name")).sendKeys(CredentialManager.getUsername());
        driver.findElement(By.id("password")).sendKeys(CredentialManager.getPassword());
        driver.findElement(By.id("login-button")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"),
            "Login failed!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}