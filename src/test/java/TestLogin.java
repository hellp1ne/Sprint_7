import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestLogin {
    private JsonObject json;
    private int randomIntInRange = ThreadLocalRandom.current().nextInt(1000);

    @Before
    @Step("Set up test environment and create a courier")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // Create JSON for courier creation and login
        json = createCourierJson("misterLogin" + randomIntInRange, "1234");

        // Create a courier
        createCourier(json);
    }

    @Test
    @Step("Check login and validate response body")
    public void checkLoginAndBodyAnswer() {
        Response loginResponse = loginCourier(json);
        validateSuccessfulLogin(loginResponse);
    }

    @Test
    @Step("Check login with wrong credentials")
    public void checkWrongLogin() {
        json.addProperty("login", "unknownLogin" + randomIntInRange);
        Response loginResponse = loginCourier(json);
        validateErrorResponse(loginResponse, 404, "Учетная запись не найдена");
    }

    @Test
    @Step("Check login with wrong password")
    public void checkWrongPassword() {
        json.addProperty("password", "wrongPassword");
        Response loginResponse = loginCourier(json);
        validateErrorResponse(loginResponse, 404, "Учетная запись не найдена");
    }

    @Test
    @Step("Check login without login field")
    public void checkWithoutLogin() {
        json.remove("login");
        Response loginResponse = loginCourier(json);
        validateErrorResponse(loginResponse, 400, "Недостаточно данных для входа");
    }

    @Test
    @Step("Check login without password field")
    public void checkWithoutPassword() {
        json.remove("password");
        Response loginResponse = loginCourier(json);
        validateErrorResponse(loginResponse, 400, "Недостаточно данных для входа");
    }

    @After
    @Step("Clean up: Delete the created courier")
    public void shutDown() {
        // Prepare data for login request
        json.remove("firstName");
        json.addProperty("login", "misterLogin" + randomIntInRange);
        json.addProperty("password", "1234");

        // Log in and get courier ID
        String courierId = getCourierId(json);

        // Delete the courier
        deleteCourier(courierId);
    }

    @Step("Create JSON for courier")
    public JsonObject createCourierJson(String login, String password) {
        String jsonString = "{\"login\":\"" + login + "\", \"password\":\"" + password + "\"}";
        JsonElement rootElement = JsonParser.parseString(jsonString);
        return rootElement.getAsJsonObject();
    }

    @Step("Create a courier")
    public void createCourier(JsonObject json) {
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Send POST request to log in courier")
    public Response loginCourier(JsonObject json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login");
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
    public String getCourierId(JsonObject json) {
        Response loginResponse = loginCourier(json);
        JsonPath jsonPath = loginResponse.jsonPath();
        return jsonPath.getString("id");
    }

    @Step("Delete courier by ID")
    public void deleteCourier(String courierId) {
        JsonObject deleteJson = new JsonObject();
        deleteJson.addProperty("id", courierId);

        given()
                .header("Content-type", "application/json")
                .body(deleteJson)
                .when()
                .delete("/api/v1/courier/:id");
    }
}