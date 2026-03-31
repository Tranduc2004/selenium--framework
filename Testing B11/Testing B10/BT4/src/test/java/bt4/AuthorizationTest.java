package bt4;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;

public class AuthorizationTest extends ApiBaseTest {

    @Test(description = "BT4.A1 - Login thanh cong")
    public void testLoginSuccess() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");
        body.put("password", "cityslicka");

        given()
                .spec(requestSpec)
                .body(body)
        .when()
                .post("/login")
        .then()
                .statusCode(200)
                .body("token", not(blankOrNullString()));
    }

    @Test(description = "BT4.A2 - Login thieu password")
    public void testLoginMissingPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");

        given()
                .spec(requestSpec)
                .body(body)
        .when()
                .post("/login")
        .then()
                .statusCode(400)
                .body("error", containsString("Missing password"));
    }

    @Test(description = "BT4.A3 - Login thieu email")
    public void testLoginMissingEmail() {
        Map<String, String> body = new HashMap<>();
        body.put("password", "cityslicka");

        given()
                .spec(requestSpec)
                .body(body)
        .when()
                .post("/login")
        .then()
                .statusCode(400)
                .body("error", containsString("Missing email or username"));
    }

    @Test(description = "BT4.A4 - Register thanh cong")
    public void testRegisterSuccess() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "eve.holt@reqres.in");
        body.put("password", "cityslicka");

        given()
                .spec(requestSpec)
                .body(body)
        .when()
                .post("/register")
        .then()
                .statusCode(200)
                .body("id", not(blankOrNullString()))
                .body("token", not(blankOrNullString()));
    }

    @Test(description = "BT4.A5 - Register thieu password")
    public void testRegisterMissingPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "sydney@fife");

        given()
                .spec(requestSpec)
                .body(body)
        .when()
                .post("/register")
        .then()
                .statusCode(400)
                .body("error", containsString("Missing password"));
    }
}
