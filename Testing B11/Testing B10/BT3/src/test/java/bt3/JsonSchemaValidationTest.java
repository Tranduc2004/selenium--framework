package bt3;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class JsonSchemaValidationTest extends ApiBaseTest {

    @Test(description = "BT3.1 - Validate GET /api/users schema")
    public void testUserListSchema() {
        given()
                .spec(requestSpec)
                .queryParam("page", 1)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-list-schema.json"));
    }

    @Test(description = "BT3.2 - Validate GET /api/users/2 schema")
    public void testSingleUserSchema() {
        given()
                .spec(requestSpec)
        .when()
                .get("/users/2")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test(description = "BT3.3 - Validate POST /api/users schema")
    public void testCreateUserSchema() {
                Map<String, Object> request = new HashMap<>();
                request.put("name", "Schema User");
                request.put("job", "QA");

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post("/users")
        .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"));
    }

    @Test(description = "BT3 Demo - Fail when schema misses a response field")
    public void testSchemaShouldFailWhenFieldMissingInSchema() {
        AssertionError error = Assert.expectThrows(AssertionError.class, () ->
                given()
                        .spec(requestSpec)
                .when()
                        .get("/users/2")
                .then()
                        .statusCode(200)
                        .body(matchesJsonSchemaInClasspath("schemas/user-schema-demo-missing-avatar.json"))
        );

        Assert.assertTrue(
                error.getMessage().contains("avatar") || error.getMessage().contains("additional") || error.getMessage().contains("schema"),
                "Expected a clear schema mismatch message mentioning missing/extra field details"
        );
    }
}
