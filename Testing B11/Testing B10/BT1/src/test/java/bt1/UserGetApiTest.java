package bt1;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;

public class UserGetApiTest extends ApiBaseTest {

    @Test(description = "BT1 - Test 1: GET /api/users?page=1")
    public void testGetUsersPage1() {
        given()
                .spec(requestSpec)
                .queryParam("page", 1)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .body("page", equalTo(1))
                .body("total_pages", greaterThan(0))
                .body("data.size()", greaterThan(0));
    }

    @Test(description = "BT1 - Test 2: GET /api/users?page=2")
    public void testGetUsersPage2RequiredFields() {
        given()
                .spec(requestSpec)
                .queryParam("page", 2)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .body("page", equalTo(2))
                .body("data.id", Matchers.everyItem(Matchers.notNullValue()))
                .body("data.email", Matchers.everyItem(Matchers.notNullValue()))
                .body("data.first_name", Matchers.everyItem(Matchers.notNullValue()))
                .body("data.last_name", Matchers.everyItem(Matchers.notNullValue()))
                .body("data.avatar", Matchers.everyItem(Matchers.notNullValue()));
    }

    @Test(description = "BT1 - Test 3: GET /api/users/3")
    public void testGetUserById3() {
        given()
                .spec(requestSpec)
        .when()
                .get("/users/3")
        .then()
                .statusCode(200)
                .body("data.id", equalTo(3))
                .body("data.email", matchesPattern("^[A-Za-z0-9._%+-]+@reqres\\.in$"))
                .body("data.first_name", Matchers.not(emptyOrNullString()));
    }

    @Test(description = "BT1 - Test 4: GET /api/users/9999")
    public void testGetUserNotFound() {
        given()
                .spec(requestSpec)
        .when()
                .get("/users/9999")
        .then()
                .statusCode(404)
                .body("$", anEmptyMap());
    }
}
