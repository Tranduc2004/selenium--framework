// ScreenshotUtil.java
package framework.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil - Tiện ích chụp ảnh màn hình khi test fail.
 * <p>
 * Lưu ảnh vào thư mục {@code target/screenshots/} với tên
 * {@code {testName}_{timestamp}.png} để dễ theo dõi lịch sử lỗi.
 * </p>
 */
public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "target/screenshots/";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Chụp ảnh màn hình hiện tại và lưu vào {@code target/screenshots/}.
     * Tên file: {@code {testName}_{timestamp}.png}
     *
     * @param driver   WebDriver instance đang chạy test
     * @param testName Tên test method để đặt tên file ảnh
     */
    public static void capture(WebDriver driver, String testName) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String destPath = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
            Files.copy(srcFile.toPath(), Paths.get(destPath));
            System.out.println("[Screenshot] Đã lưu: " + destPath);
        } catch (IOException e) {
            System.err.println("[Screenshot] Lỗi khi lưu ảnh: " + e.getMessage());
        }
    }

    /**
     * Chụp ảnh màn hình và trả về dạng byte array (dùng cho Allure report).
     *
     * @param driver WebDriver instance đang chạy test
     * @return Mảng byte chứa dữ liệu ảnh PNG
     */
    public static byte[] captureAsBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
