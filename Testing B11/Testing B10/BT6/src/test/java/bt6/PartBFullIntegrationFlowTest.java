package bt6;

import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PartBFullIntegrationFlowTest extends ApiBaseTest {

    private final UiBaseTest ui = new UiBaseTest();
    private boolean isApiAlive;

    @BeforeMethod
    public void checkUsersApiHealth() {
        // [API CHECK][Capture-B1] Verify ReqRes is alive via GET /users.
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        isApiAlive = response.statusCode() == 200;
        // [API CHECK][Capture-B2] Console log that proves API health status and isApiAlive flag.
        System.out.printf("[PartB][API] GET /users status=%d, isApiAlive=%s%n", response.statusCode(), isApiAlive);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        ui.closeBrowserIfOpened();
    }

    @Test(description = "Part B - API alive then UI login, add 2 products, verify cart")
    public void fullApiUiIntegrationFlow() {
        // API check: if ReqRes is not healthy, skip all UI actions.
        // [PRECONDITION][Capture-B3] SkipException path for unavailable API.
        if (!isApiAlive) {
            throw new SkipException("ReqRes API is not alive. Skip UI flow.");
        }

        // [UI ACTION][Capture-B4] Login by real form input (no injection).
        ui.setupBrowser();
        ui.loginSauceDemoByForm("standard_user", "secret_sauce");

        // [UI ACTION][Capture-B5] Add exactly 2 products.
        ui.wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))).click();
        ui.wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-bike-light"))).click();
        ui.captureScreenshot("partB-added-2-products");

        // [ASSERTION][Capture-B6] Cart badge must be 2.
        String badgeText = ui.wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge"))).getText();
        Assert.assertEquals(badgeText, "2", "Cart badge must be 2");
        ui.captureScreenshot("partB-badge-equals-2");

        // [UI ACTION][Capture-B7] Open cart page.
        ui.driver.findElement(By.className("shopping_cart_link")).click();

        // [ASSERTION][Capture-B8] Cart must contain exactly 2 items.
        int itemCount = ui.wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("cart_item"))).size();
        Assert.assertEquals(itemCount, 2, "Cart must contain exactly 2 products");
        ui.captureScreenshot("partB-cart-has-2-items");
    }
}
