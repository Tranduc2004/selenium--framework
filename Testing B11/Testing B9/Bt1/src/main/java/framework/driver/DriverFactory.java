package framework.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * DriverFactory - Tạo WebDriver theo browser và tự bật headless trên CI.
 */
public class DriverFactory {

    private DriverFactory() {
    }

    /**
     * Tạo WebDriver theo tên browser.
     * Nếu chạy trên CI (biến môi trường CI=true), sẽ tự bật headless.
     */
    public static WebDriver createDriver(String browser) {
        boolean isCi = System.getenv("CI") != null;

        switch (browser.toLowerCase()) {
            case "firefox":
                return createFirefoxDriver(isCi);
            case "chrome":
            default:
                return createChromeDriver(isCi);
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }

        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }

        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(options);
    }
}