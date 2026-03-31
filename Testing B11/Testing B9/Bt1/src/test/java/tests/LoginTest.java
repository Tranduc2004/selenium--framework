// LoginTest.java
package tests;

import framework.base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest - Test class kiểm tra trang đăng nhập saucedemo.com.
 * <p>
 * Mục đích Bài 1: Xác minh BasePage và BaseTest hoạt động đúng.
 * <ul>
 *   <li>testPageLoads - PASS: kiểm tra trang load được</li>
 *   <li>testLoginButtonVisible - PASS: kiểm tra nút Login hiển thị</li>
 *   <li>testFailToTriggerScreenshot - FAIL có ý thức: xác minh ảnh chụp xuất hiện trong target/screenshots/</li>
 * </ul>
 * </p>
 * Ba method chạy song song (parallel="methods" thread-count="3") mở 3 Chrome cùng lúc.
 */
public class LoginTest extends BaseTest {

    /**
     * Kiểm tra trang saucedemo.com load thành công và có đúng title.
     * Kỳ vọng PASS.
     */
    @Test
    public void testPageLoads() {
        String title = getDriver().getTitle();
        Assert.assertTrue(title.contains("Swag Labs"),
                "Trang Swag Labs chưa load đúng - title hiện tại: " + title);
    }

    /**
     * Kiểm tra nút Login hiển thị trên trang đăng nhập.
     * Kỳ vọng PASS.
     */
    @Test
    public void testLoginButtonVisible() {
        boolean loginBtnVisible = getDriver()
                .findElement(By.id("login-button"))
                .isDisplayed();
        Assert.assertTrue(loginBtnVisible, "Nút Login không hiển thị trên trang");
    }

    /**
     * Test FAIL có ý thức để xác minh tính năng chụp ảnh màn hình khi fail.
     * Sau khi chạy, kiểm tra file PNG xuất hiện trong thư mục target/screenshots/.
     * Kỳ vọng FAIL khi bật -DintentionalFail=true.
     */
    @Test
    public void testFailToTriggerScreenshot() {
        String actualTitle = getDriver().getTitle();
        boolean intentionalFail = Boolean.parseBoolean(System.getProperty("intentionalFail", "false"));

        if (intentionalFail) {
            // Assertion sai có ý thức để trigger chụp ảnh màn hình
            Assert.assertEquals(actualTitle, "WRONG_TITLE_TO_TRIGGER_SCREENSHOT",
                    "Test fail có ý thức - kiểm tra ảnh chụp trong target/screenshots/");
        } else {
            Assert.assertTrue(actualTitle.contains("Swag Labs"),
                    "intentionalFail=false: giữ pipeline xanh mặc định");
        }
    }
}
