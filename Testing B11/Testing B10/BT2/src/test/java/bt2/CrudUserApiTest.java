package bt2;

import bt2.model.CreateUserRequest;
import bt2.model.UserResponse;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class CrudUserApiTest extends ApiBaseTest {

    @Test(description = "BT2.1 - POST /api/users tao user")
    public void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest("BT2_User_" + UUID.randomUUID(), "QA Engineer");

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post("/users")
        .then()
                .statusCode(201)
                .body("name", equalTo(request.getName()))
                .body("job", equalTo(request.getJob()))
                .body("id", not(blankOrNullString()))
                .body("createdAt", not(blankOrNullString()));
    }

    @Test(description = "BT2.2 - PUT /api/users/{id} cap nhat user")
    public void testUpdateUserByPut() {
        UserResponse created = createUser("PUT_Initial", "Junior QA");
        String userId = created.getId();

        CreateUserRequest updateRequest = new CreateUserRequest("PUT_Updated", "Senior QA");

        UserResponse updated =
                given()
                        .spec(requestSpec)
                        .body(updateRequest)
                .when()
                        .put("/users/{id}", userId)
                .then()
                        .statusCode(200)
                        .body("job", equalTo(updateRequest.getJob()))
                        .body("updatedAt", notNullValue())
                        .extract()
                        .as(UserResponse.class);

        Assert.assertNotNull(updated.getUpdatedAt(), "updatedAt must not be null");
        Assert.assertNotEquals(updated.getUpdatedAt(), created.getCreatedAt(), "updatedAt should differ from createdAt");
    }

    @Test(description = "BT2.3 - PATCH /api/users/{id} cap nhat mot phan")
    public void testPatchUserPartialUpdate() {
        UserResponse created = createUser("PATCH_Initial", "Manual QA");

        Map<String, Object> patchBody = new HashMap<>();
        patchBody.put("job", "Automation QA");

        String oldTimestamp = created.getCreatedAt();

        String updatedAt =
                given()
                        .spec(requestSpec)
                        .body(patchBody)
                .when()
                        .patch("/users/{id}", created.getId())
                .then()
                        .statusCode(200)
                        .body("job", equalTo("Automation QA"))
                        .body("updatedAt", not(blankOrNullString()))
                        .extract()
                        .path("updatedAt");

        Assert.assertTrue(Instant.parse(updatedAt).isAfter(Instant.parse(oldTimestamp)), "updatedAt must be newer than createdAt");
    }

    @Test(description = "BT2.4 - DELETE /api/users/{id} xoa user")
    public void testDeleteUser() {
        UserResponse created = createUser("DELETE_Initial", "QA");

        Response deleteResponse =
                given()
                        .spec(requestSpec)
                .when()
                        .delete("/users/{id}", created.getId())
                .then()
                        .statusCode(204)
                        .extract()
                        .response();

        Assert.assertTrue(deleteResponse.asString().isBlank(), "DELETE response body must be empty");
    }

    @Test(description = "BT2.5 - POST -> GET xac nhan")
    public void testPostThenGetVerify() {
        String existingFirstName =
                given()
                        .spec(requestSpec)
                .when()
                        .get("/users/2")
                .then()
                        .statusCode(200)
                        .extract()
                        .path("data.first_name");

        CreateUserRequest request = new CreateUserRequest(existingFirstName, "API Tester");

        UserResponse created =
                given()
                        .spec(requestSpec)
                        .body(request)
                .when()
                        .post("/users")
                .then()
                        .statusCode(201)
                        .body("id", not(blankOrNullString()))
                        .extract()
                        .as(UserResponse.class);

        given()
                .spec(requestSpec)
        .when()
                .get("/users/2")
        .then()
                .statusCode(200)
                .body("data.first_name", equalTo(request.getName()));

        Assert.assertNotNull(created.getId(), "Created user id must not be null");
    }

    private UserResponse createUser(String namePrefix, String job) {
        CreateUserRequest request = new CreateUserRequest(namePrefix + "_" + UUID.randomUUID(), job);

        UserResponse response =
                given()
                        .spec(requestSpec)
                        .body(request)
                .when()
                        .post("/users")
                .then()
                        .statusCode(201)
                        .extract()
                        .as(UserResponse.class);

        Assert.assertNotNull(response.getId(), "id must not be null");
        Assert.assertNotNull(response.getCreatedAt(), "createdAt must not be null");
        return response;
    }
}
