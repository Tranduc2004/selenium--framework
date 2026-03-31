// BaseTest.java
package framework.base;

import framework.config.ConfigReader;
import framework.driver.DriverFactory;
import framework.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

/**
 * BaseTest - Quản lý vòng đời WebDriver cho mỗi test method.
 * <p>
 * Dùng {@link ThreadLocal} để hỗ trợ chạy song song (parallel="methods")
 * an toàn - mỗi thread có WebDriver riêng, không chia sẻ state.
 * </p>
 * <p>
 * Tự động chụp ảnh màn hình vào {@code target/screenshots/} khi test FAIL
 * để hỗ trợ debug. Mọi test class đều {@code extends BaseTest}.
 * </p>
 */
public abstract class BaseTest {

    /**
     * ThreadLocal đảm bảo mỗi thread có WebDriver riêng biệt.
     * KHÔNG dùng biến static thông thường vì sẽ gây race condition khi chạy song song.
     */
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    /**
     * Lấy WebDriver của thread hiện tại.
     *
     * @return WebDriver instance đang chạy trong thread này
     */
    protected WebDriver getDriver() {
        return tlDriver.get();
    }

    /**
     * Khởi tạo WebDriver trước mỗi test method.
     * <p>
     * Nhận tham số {@code browser} và {@code env} từ {@code testng.xml};
     * nếu không khai báo thì dùng giá trị mặc định {@code "chrome"} và {@code "dev"}.
     * Đặt {@code env} vào System property để {@link ConfigReader} đọc đúng file config.
     * </p>
     *
     * @param browser Tên trình duyệt ("chrome" hoặc "firefox"), mặc định "chrome"
     * @param env     Môi trường chạy test ("dev", "staging", "prod"), mặc định "dev"
     */
    @Parameters({"browser", "env"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser,
                      @Optional("dev") String env) {
        // Đặt env làm System property để ConfigReader đọc đúng file
        System.setProperty("env", env);

        WebDriver driver = DriverFactory.createDriver(browser);
        if (System.getenv("CI") == null) {
            driver.manage().window().maximize();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(ConfigReader.getInstance().getBaseUrl());
        tlDriver.set(driver);
    }

    /**
     * Dọn dẹp sau mỗi test method.
     * <p>
     * Chụp ảnh màn hình với tên {@code {testName}_{timestamp}.png} khi test FAIL
     * và lưu vào {@code target/screenshots/} để debug.
     * Luôn quit WebDriver và xóa khỏi ThreadLocal để tránh memory leak
     * khi chạy nhiều test song song.
     * </p>
     *
     * @param result Kết quả của test method vừa chạy (PASS / FAIL / SKIP)
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Chụp ảnh TRƯỚC khi quit - bắt buộc trong dự án thực để debug
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtil.capture(getDriver(), result.getName());
        }

        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove(); // Quan trọng: tránh memory leak khi chạy song song
        }
    }
}
