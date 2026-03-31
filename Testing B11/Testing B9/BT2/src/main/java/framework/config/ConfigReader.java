// ConfigReader.java
package framework.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Singleton đọc file cấu hình theo môi trường.
 * <p>
 * Đọc đúng file {@code config-{env}.properties} dựa trên System property {@code "env"}
 * được truyền vào khi chạy (dev / staging / prod). Mặc định dùng {@code "dev"}.
 * </p>
 * <p>
 * Ví dụ chạy staging từ command line: {@code mvn test -Denv=staging}
 * </p>
 */
public class ConfigReader {

    private static final Properties props = new Properties();
    private static ConfigReader instance;

    private ConfigReader() {
        String env = System.getProperty("env", "dev");
        String file = "src/test/resources/config-" + env + ".properties";
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
            System.out.println("[ConfigReader] Đang dùng môi trường: " + env);
        } catch (IOException e) {
            throw new RuntimeException("Không tìm thấy config: " + file, e);
        }
    }

    /**
     * Lấy instance duy nhất của ConfigReader (Singleton Pattern).
     * Thread-safe nhờ {@code synchronized}.
     *
     * @return ConfigReader instance
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) instance = new ConfigReader();
        return instance;
    }

    /** @return URL gốc của ứng dụng cần test (ví dụ: https://www.saucedemo.com) */
    public String getBaseUrl()        { return props.getProperty("base.url"); }

    /** @return Tên trình duyệt, mặc định {@code "chrome"} */
    public String getBrowser()        { return props.getProperty("browser", "chrome"); }

    /** @return Thời gian Explicit Wait tối đa (giây), mặc định 15 */
    public int getExplicitWait()      { return Integer.parseInt(props.getProperty("explicit.wait", "15")); }

    /** @return Số lần retry khi test fail, mặc định 1 */
    public int getRetryCount()        { return Integer.parseInt(props.getProperty("retry.count", "1")); }

    /** @return Đường dẫn thư mục lưu screenshot */
    public String getScreenshotPath() { return props.getProperty("screenshot.path", "target/screenshots/"); }
}
