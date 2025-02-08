import api.CourierApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import json.CreateCourierRequest;
import json.LoginCourierRequest;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;

public class TestCreateCourier {
    private Response response;
    private int randomIntInRange = ThreadLocalRandom.current().nextInt(1000000);
    private CourierApiClient courierApiClient = new CourierApiClient();

    @Test
    public void checkStatusCodeAndSuccessBodyAnswerTest() {
        // Create JSON for courier with all fields
        CreateCourierRequest courierRequest = createCourierJson("misterLogin" + randomIntInRange, "1234", "Adel");

        // Create courier
        response = courierApiClient.createCourier(courierRequest);

        // Validate successful courier creation
        validateSuccessfulCourierCreation(response);
    }

    @Test
    public void checkNecessaryObjectsTest() {
        // Create JSON for courier with necessary fields only
        CreateCourierRequest courierRequest = createCourierJson("misterLogin" + randomIntInRange, "1234", null);

        // Create courier
        response = courierApiClient.createCourier(courierRequest);

        // Validate successful courier creation
        validateSuccessfulCourierCreation(response);
    }

    @Test
    public void createCouriersWithSameLoginTest() {
        // Create JSON for courier with all fields
        CreateCourierRequest courierRequest = createCourierJson("misterLogin" + randomIntInRange, "1234", null);

        // Attempt to create courier twice
        for (int i = 0; i < 2; ++i) {
            response = courierApiClient.createCourier(courierRequest);
        }

        // Validate error response for duplicate login
        validateErrorResponse(response, 409, "Этот логин уже используется. Попробуйте другой.");
    }

    @Test
    public void createCourierWithoutLoginTest() {
        // Create JSON for courier without login
        CreateCourierRequest courierRequest = createCourierJson(null, "1234", null);

        // Create courier
        response = courierApiClient.createCourier(courierRequest);

        // Validate error response for missing login
        validateErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    public void createCourierWithoutPasswordTest() {
        // Create JSON for courier without password
        CreateCourierRequest courierRequest = createCourierJson("misterLogin" + randomIntInRange, null, null);

        // Create courier
        response = courierApiClient.createCourier(courierRequest);

        // Validate error response for missing password
        validateErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @After
    public void shutDown() {
        String statusCode = String.valueOf(response.statusCode());
        if (statusCode.startsWith("2") || statusCode.equals("409")) {
            // Prepare data for login request
            LoginCourierRequest loginRequest = new LoginCourierRequest(
                    "misterLogin" + randomIntInRange, "1234"
            );

            // Extract courier ID and delete the courier
            String courierId = extractCourierId(loginRequest);
            courierApiClient.deleteCourier(courierId);
        }
    }

    @Step("Create JSON for courier with different keys")
    public CreateCourierRequest createCourierJson(String login, String password, String firstName) {
        return new CreateCourierRequest(login, password, firstName);
    }

    @Step("Validate successful courier creation response")
    public void validateSuccessfulCourierCreation(Response response) {
        response.then()
                .statusCode(201)
                .and()
                .assertThat()
                .body("ok", equalTo(true));
    }

    @Step("Validate error response with status code: {statusCode} and message: {message}")
    public void validateErrorResponse(Response response, int statusCode, String message) {
        response.then()
                .statusCode(statusCode)
                .and()
                .assertThat()
                .body("message", equalTo(message));
    }

    @Step("Extract courier ID from login response")
    public String extractCourierId(LoginCourierRequest loginRequest) {
        response = courierApiClient.loginCourier(loginRequest);
        return response.jsonPath().getString("id");
    }
}