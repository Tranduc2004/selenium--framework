package bt1;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.SkipException;

public class ApiBaseTest {

    protected RequestSpecification requestSpec;
    protected String apiKey;

    @BeforeClass
    public void setupApiSpec() {
        apiKey = resolveApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new SkipException("Missing ReqRes API key. Set REQRES_API_KEY or -Dreqres.api.key=<your_key>.");
        }

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://reqres.in")
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .addHeader("reqres_b39fc6f91e47413d846fda0487449582", apiKey)
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
