import api.CourierApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import json.CreateCourierRequest;
import json.LoginCourierRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;

public class TestLogin {
    private int randomIntInRange = ThreadLocalRandom.current().nextInt(1000);
    private String login = "misterLogin" + randomIntInRange;
    private String password = "1234";
    private CourierApiClient courierApiClient = new CourierApiClient();

    @Before
    public void setUp() {
        // Create a courier
        CreateCourierRequest courierRequest = new CreateCourierRequest(login, password, null);
        courierApiClient.createCourier(courierRequest);
    }

    @Test
    public void checkLoginAndBodyAnswerTest() {
        LoginCourierRequest loginRequest = new LoginCourierRequest(login, password);
        Response loginResponse = courierApiClient.loginCourier(loginRequest);
        validateSuccessfulLogin(loginResponse);
    }

    @Test
    public void checkWrongLoginTest() {
        LoginCourierRequest loginRequest = new LoginCourierRequest("unknownLogin" + randomIntInRange, password);
        Response loginResponse = courierApiClient.loginCourier(loginRequest);
        validateErrorResponse(loginResponse, 404, "Учетная запись не найдена");
    }

    @Test
    public void checkWrongPasswordTest() {
        LoginCourierRequest loginRequest = new LoginCourierRequest(login, "wrongPassword");
        Response loginResponse = courierApiClient.loginCourier(loginRequest);
        validateErrorResponse(loginResponse, 404, "Учетная запись не найдена");
    }

    @Test
    public void checkWithoutLoginTest() {
        LoginCourierRequest loginRequest = new LoginCourierRequest(null, password);
        Response loginResponse = courierApiClient.loginCourier(loginRequest);
        validateErrorResponse(loginResponse, 400, "Недостаточно данных для входа");
    }

    @Test
    public void checkWithoutPasswordTest() {
        LoginCourierRequest loginRequest = new LoginCourierRequest(login, null);
        Response loginResponse = courierApiClient.loginCourier(loginRequest);
        validateErrorResponse(loginResponse, 400, "Недостаточно данных для входа");
    }

    @After
    public void shutDown() {
        // Log in and get courier ID
        LoginCourierRequest loginRequest = new LoginCourierRequest(login, password);
        String courierId = getCourierId(loginRequest);

        // Delete the courier
        courierApiClient.deleteCourier(courierId);
    }

    @Step("Validate successful login response")
    public void validateSuccessfulLogin(Response response) {
        response.then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("id", notNullValue());
    }

    @Step("Validate error response with status code and message")
    public void validateErrorResponse(Response response, int statusCode, String message) {
        response.then()
                .statusCode(statusCode)
                .and()
                .assertThat()
                .body("message", equalTo(message));
    }

    @Step("Get courier ID from login response")
    public String getCourierId(LoginCourierRequest loginRequest) {
        Response loginResponse = courierApiClient.loginCourier(loginRequest);
        return loginResponse.jsonPath().getString("id");
    }
}