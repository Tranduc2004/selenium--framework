package bt5;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;

public class PerformanceSlaTest extends ApiBaseTest {

    @DataProvider(name = "slaScenarios")
    public Object[][] slaScenarios() {
        return new Object[][] {
                {"GET", "/users", 2000L, 200, "USERS_LIST"},
                {"GET", "/users/2", 1500L, 200, "USER_BY_ID"},
                {"POST", "/users", 3000L, 201, "CREATE_USER"},
                {"POST", "/login", 2000L, 200, "LOGIN"},
                {"DELETE", "/users/2", 1000L, 204, "DELETE_USER"}
        };
    }

    @Test(dataProvider = "slaScenarios", description = "BT5 - Performance assertion va SLA monitoring")
    public void testSlaForMainEndpoints(String method, String endpoint, long maxMs, int expectedStatus, String assertionType) {
        Response response = callEndpoint(method, endpoint, maxMs);
        long elapsedMs = response.time();

        // Retry once to avoid flaky failures caused by transient network spikes.
        if (elapsedMs > maxMs) {
            System.out.printf("[SLA-RETRY] %s %s first call exceeded: actual=%dms, max=%dms. Retrying once...%n",
                method, endpoint, elapsedMs, maxMs);
            response = callEndpoint(method, endpoint, maxMs);
            elapsedMs = response.time();
        }

        int statusCode = response.statusCode();
        System.out.printf("[SLA] %s %s | actual=%dms | max=%dms | status=%d%n", method, endpoint, elapsedMs, maxMs, statusCode);

        Assert.assertTrue(elapsedMs <= maxMs,
            String.format("SLA violated for %s %s after retry: actual=%dms, max=%dms", method, endpoint, elapsedMs, maxMs));
        Assert.assertEquals(statusCode, expectedStatus, "Unexpected status code");

        assertBusinessCondition(response, assertionType);
    }

    @Test(description = "BT5 - Monitoring GET /users 10 lan de tinh min/max/avg")
    public void monitorUsersEndpointForTenRuns() {
        final String method = "GET";
        final String endpoint = "/users";
        final long maxMs = 2000L;
        final int runs = 10;

        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long sum = 0;

        for (int i = 1; i <= runs; i++) {
            Response response = callEndpoint(method, endpoint, maxMs);
            long elapsed = response.time();

            Assert.assertEquals(response.statusCode(), 200, "Unexpected status code in monitoring run");
            response.then().body("data.size()", greaterThanOrEqualTo(1));

            min = Math.min(min, elapsed);
            max = Math.max(max, elapsed);
            sum += elapsed;

            System.out.printf("[MONITOR] run=%d | endpoint=%s | responseTime=%dms%n", i, endpoint, elapsed);
        }

        double avg = (double) sum / runs;
        System.out.printf("[MONITOR SUMMARY] endpoint=%s | runs=%d | min=%dms | max=%dms | avg=%.2fms%n",
                endpoint, runs, min, max, avg);
    }

    @Step("Goi {method} {endpoint} - SLA: {maxMs} ms")
    private Response callEndpoint(String method, String endpoint, long maxMs) {
        switch (method) {
            case "GET":
                return given()
                        .spec(requestSpec)
                        .when()
                        .get(endpoint)
                        .then()
                        .extract()
                        .response();
            case "POST":
                return given()
                        .spec(requestSpec)
                        .body(buildBodyByEndpoint(endpoint))
                        .when()
                        .post(endpoint)
                        .then()
                        .extract()
                        .response();
            case "DELETE":
                return given()
                        .spec(requestSpec)
                        .when()
                        .delete(endpoint)
                        .then()
                        .extract()
                        .response();
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }

    private Map<String, String> buildBodyByEndpoint(String endpoint) {
        Map<String, String> body = new HashMap<>();
        if ("/users".equals(endpoint)) {
            body.put("name", "morpheus");
            body.put("job", "leader");
        } else if ("/login".equals(endpoint)) {
            body.put("email", "eve.holt@reqres.in");
            body.put("password", "cityslicka");
        }
        return body;
    }

    private void assertBusinessCondition(Response response, String assertionType) {
        switch (assertionType) {
            case "USERS_LIST":
                response.then().body("data.size()", greaterThanOrEqualTo(1));
                break;
            case "USER_BY_ID":
                response.then().body("data.id", equalTo(2));
                break;
            case "CREATE_USER":
                response.then().body("id", not(blankOrNullString()));
                break;
            case "LOGIN":
                response.then().body("token", not(blankOrNullString()));
                break;
            case "DELETE_USER":
                // 204 No Content, no body assertion needed.
                break;
            default:
                throw new IllegalArgumentException("Unsupported assertion type: " + assertionType);
        }
    }
}
