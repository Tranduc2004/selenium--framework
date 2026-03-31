// BasePage.java
package framework.base;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage - Lớp nền tảng cho mọi Page Object.
 * <p>
 * Đóng gói tất cả các thao tác WebDriver phổ biến thành các method
 * có Explicit Wait tích hợp sẵn. Mọi Page Object đều {@code extends BasePage}
 * để dùng lại các method này mà không phải viết lại.
 * </p>
 * <p>
 * Lưu ý: KHÔNG dùng {@code Thread.sleep()} ở bất kỳ chỗ nào trong class này.
 * Mọi chờ đợi đều thông qua {@link WebDriverWait} và {@link ExpectedConditions}.
 * </p>
 */
public abstract class BasePage {

    /** WebDriver instance được chia sẻ cho toàn bộ Page Object. */
    protected WebDriver driver;

    /** Explicit Wait dùng chung, timeout mặc định 15 giây. */
    protected WebDriverWait wait;

    /**
     * Khởi tạo BasePage với WebDriver được truyền vào.
     * Thiết lập Explicit Wait 15 giây và khởi tạo các {@code @FindBy} annotations
     * thông qua {@link PageFactory}.
     *
     * @param driver WebDriver instance đang được sử dụng - không được {@code null}
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Explicit Wait 15 giây - đủ cho mạng chậm, không chờ lãng phí
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this); // Khởi tạo @FindBy annotations
    }

    /**
     * Chờ element clickable rồi mới click - tránh {@code ElementNotInteractableException}.
     * <p>
     * Sử dụng {@link ExpectedConditions#elementToBeClickable(WebElement)} để đảm bảo
     * element đã hiển thị và enabled trước khi thực hiện click. Thường dùng cho button,
     * link, checkbox.
     * </p>
     *
     * @param element WebElement cần click - phải là element tương tác được
     * @throws org.openqa.selenium.TimeoutException nếu element không clickable trong 15 giây
     */
    protected void waitAndClick(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /**
     * Xóa nội dung cũ rồi gõ text mới - tránh dữ liệu bị nối thêm vào input.
     * <p>
     * Chờ element visible trước, sau đó {@code clear()} để xóa nội dung hiện tại,
     * rồi {@code sendKeys(text)} để nhập liệu mới. Dùng cho input, textarea.
     * </p>
     *
     * @param element WebElement là input hoặc textarea cần nhập liệu
     * @param text    Chuỗi văn bản cần gõ vào element
     * @throws org.openqa.selenium.TimeoutException nếu element không visible trong 15 giây
     */
    protected void waitAndType(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Lấy text đã trim whitespace của element.
     * <p>
     * Chờ element visible trước khi lấy text để tránh nhận chuỗi rỗng
     * khi element chưa render xong hoặc content chưa được load.
     * </p>
     *
     * @param element WebElement cần lấy text
     * @return Chuỗi text đã được trim, không có khoảng trắng đầu/cuối
     * @throws org.openqa.selenium.TimeoutException nếu element không visible trong 15 giây
     */
    protected String getText(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element)).getText().trim();
    }

    /**
     * Kiểm tra hiển thị - KHÔNG throw exception nếu không tìm thấy element.
     * <p>
     * Xử lý {@link StaleElementReferenceException} xảy ra khi trang render lại DOM
     * (ví dụ: sau AJAX cập nhật, element cũ bị remove khỏi DOM và tạo mới).
     * Xử lý {@link NoSuchElementException} khi locator không tìm thấy element nào.
     * </p>
     *
     * @param locator By locator để tìm element cần kiểm tra
     * @return {@code true} nếu element tồn tại và đang hiển thị,
     *         {@code false} nếu không tìm thấy hoặc bị stale
     */
    protected boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Cuộn trang đến element - xử lý element nằm ngoài viewport.
     * <p>
     * Sử dụng JavaScript {@code scrollIntoView(true)} để đưa element vào
     * giữa vùng nhìn thấy trước khi thực hiện các thao tác tiếp theo.
     * Cần thiết khi element bị che bởi fixed header hoặc nằm cuối trang dài.
     * </p>
     *
     * @param element WebElement cần cuộn trang đến
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Chờ trang load hoàn toàn - dùng sau khi điều hướng sang trang mới.
     * <p>
     * Sử dụng JavaScript {@code document.readyState} để xác nhận trang đã
     * hoàn tất việc tải tất cả tài nguyên (HTML, CSS, JS, images).
     * Nên gọi sau {@code driver.get(url)} hoặc sau khi click link dẫn đến trang khác.
     * </p>
     */
    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Lấy giá trị attribute của element.
     * <p>
     * Chờ element visible trước khi lấy attribute để đảm bảo element
     * đã render đầy đủ và attribute đã có giá trị chính xác.
     * Ví dụ dùng: lấy {@code value} của input, {@code class} để kiểm tra trạng thái,
     * {@code href} của link.
     * </p>
     *
     * @param element WebElement cần lấy attribute
     * @param attr    Tên attribute cần lấy (ví dụ: {@code "value"}, {@code "class"}, {@code "href"})
     * @return Giá trị của attribute dạng String, hoặc {@code null} nếu attribute không tồn tại
     * @throws org.openqa.selenium.TimeoutException nếu element không visible trong 15 giây
     */
    protected String getAttribute(WebElement element, String attr) {
        return wait.until(ExpectedConditions.visibilityOf(element)).getAttribute(attr);
    }
}
