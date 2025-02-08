package uri;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {
    public static RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://qa-scooter.praktikum-services.ru") // Set base URL
                .setContentType("application/json") // Set content type
                .addHeader("Accept", "application/json") // Add common headers
                .build();
}
