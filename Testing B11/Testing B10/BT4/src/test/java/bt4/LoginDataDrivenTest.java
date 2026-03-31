package bt4;

import io.restassured.response.ValidatableResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class LoginDataDrivenTest extends ApiBaseTest {

    @DataProvider(name = "loginScenarios")
    public Object[][] loginScenarios() {
        return new Object[][] {
                {"eve.holt@reqres.in", "cityslicka", 200, null},
                {"eve.holt@reqres.in", "", 400, "Missing password"},
                {"", "cityslicka", 400, "Missing email or username"},
                {"notexist@reqres.in", "wrongpass", 400, "user not found"},
                {"invalid-email", "pass123", 400, "user not found"}
        };
    }

    @Test(dataProvider = "loginScenarios", description = "BT4.B - Data-driven login error handling")
    public void testLoginScenarios(String email, String password, int expectedStatus, String expectedError) {
        Map<String, String> body = new HashMap<>();
        if (email != null && !email.isEmpty()) {
            body.put("email", email);
        }
        if (password != null && !password.isEmpty()) {
            body.put("password", password);
        }

        ValidatableResponse response =
                given()
                        .spec(requestSpec)
                        .body(body)
                .when()
                        .post("/login")
                .then()
                        .statusCode(expectedStatus);

        if (expectedError != null) {
            response.body("error", containsString(expectedError));
        }
    }
}
