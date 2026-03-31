package bt6;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;

public class ApiBaseTest {

    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setupApiSpec() {
        String apiKey = resolveApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new SkipException("Missing ReqRes API key. Set REQRES_API_KEY or -Dreqres.api.key=<your_key>.");
        }

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://reqres.in")
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .addHeader("x-api-key", apiKey)
                .addHeader("Accept", "application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private String resolveApiKey() {
        String keyFromProperty = System.getProperty("reqres.api.key");
        if (keyFromProperty != null && !keyFromProperty.isBlank()) {
            return keyFromProperty;
        }
        return System.getenv("REQRES_API_KEY");
    }
}
