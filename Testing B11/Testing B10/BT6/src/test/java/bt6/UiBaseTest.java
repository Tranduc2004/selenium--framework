package bt6;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UiBaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected void setupBrowser() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(System.getProperty("ui.headless", "false"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected void loginSauceDemoByForm(String username, String password) {
        driver.get("https://www.saucedemo.com/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        // Capture login form as soon as the page is fully ready.
        captureScreenshot("login-page-loaded");

        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();

        wait.until(ExpectedConditions.urlContains("inventory"));
        // Capture destination page after successful login.
        captureScreenshot("login-success-inventory");
    }

    protected void closeBrowserIfOpened() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    protected void captureScreenshot(String label) {
        if (driver == null) {
            return;
        }

        try {
            Path screenshotDir = Paths.get("target", "screenshots");
            Files.createDirectories(screenshotDir);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"));
            String fileName = label + "-" + timestamp + ".png";
            Path outputFile = screenshotDir.resolve(fileName);

            byte[] imageData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(outputFile, imageData);
            System.out.println("[UI][SCREENSHOT] Saved: " + outputFile.toAbsolutePath());
        } catch (IOException ex) {
            System.out.println("[UI][SCREENSHOT] Failed to save screenshot: " + ex.getMessage());
        }
    }
}
