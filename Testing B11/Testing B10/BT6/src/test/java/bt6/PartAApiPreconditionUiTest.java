package bt6;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PartAApiPreconditionUiTest extends ApiBaseTest {

    private final UiBaseTest ui = new UiBaseTest();
    private boolean isLoginApiReady;
    private String loginToken;

    @BeforeMethod
    public void loginApiPrecondition() {
        // [API CHECK][Capture-1] POST /api/login to obtain token before UI.
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");
        body.put("password", "cityslicka");

        Response response = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/login")
                .then()
                .extract()
                .response();

        isLoginApiReady = response.statusCode() == 200;
        loginToken = response.jsonPath().getString("token");

        // [API CHECK][Capture-2] Console log that proves status and token were received.
        System.out.printf("[PartA][API] status=%d, token=%s%n", response.statusCode(), loginToken);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        ui.closeBrowserIfOpened();
    }

    @Test(description = "Part A - API precondition must pass before UI")
    public void apiLoginPreconditionMustPass() {
        // [PRECONDITION][Capture-3] SkipException path when API precondition fails.
        if (!isLoginApiReady || loginToken == null || loginToken.isBlank()) {
            throw new SkipException("API login precondition failed. Skip UI verification.");
        }
    }

    @Test(dependsOnMethods = "apiLoginPreconditionMustPass", description = "Part A - UI verify URL and title after login")
    public void uiVerifyAfterApiPrecondition() {
        // [UI ACTION][Capture-4] Login by filling web form on SauceDemo (no API injection).
        ui.setupBrowser();
        ui.loginSauceDemoByForm("standard_user", "secret_sauce");

        // [ASSERTION][Capture-5] URL must contain "inventory".
        Assert.assertTrue(ui.driver.getCurrentUrl().contains("inventory"), "URL must contain 'inventory'");
        // [ASSERTION][Capture-6] Page title must be "Swag Labs".
        Assert.assertEquals(ui.driver.getTitle(), "Swag Labs", "Unexpected page title");
    }
}
