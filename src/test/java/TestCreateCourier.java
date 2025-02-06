import com.google.gson.JsonObject;
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

public class TestCreateCourier {
    private JsonObject json;
    private Response response;
    private int randomIntInRange = ThreadLocalRandom.current().nextInt(1000000);

    @Before
    @Step("Set up base URI for the API")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @Step("Check status code and success body answer for courier creation")
    public void checkStatusCodeAndSuccessBodyAnswer() {
        // Create JSON for courier with all fields
        json = createCourierJson("misterLogin" + randomIntInRange, "1234", "Adel");

        // Create courier
        response = createCourier(json);

        // Validate successful courier creation
        validateSuccessfulCourierCreation(response);
    }

    @Test
    @Step("Check creation of courier with necessary fields only")
    public void checkNecessaryObjects() {
        // Create JSON for courier with necessary fields only
        json = createCourierJson("misterLogin" + randomIntInRange, "1234", null);

        // Create courier
        response = createCourier(json);

        // Validate successful courier creation
        validateSuccessfulCourierCreation(response);
    }

    @Test
    @Step("Check creation of couriers with the same login")
    public void createCouriersWithSameLogin() {
        // Create JSON for courier with all fields
        json = createCourierJson("misterLogin" + randomIntInRange, "1234", "Adel");

        // Attempt to create courier twice
        for (int i = 0; i < 2; ++i) {
            response = createCourier(json);
        }

        // Validate error response for duplicate login
        validateErrorResponse(response, 409, "Этот логин уже используется. Попробуйте другой.");
    }

    @Test
    @Step("Check creation of courier without login")
    public void createCourierWithoutLogin() {
        // Create JSON for courier without login
        json = createCourierJson(null, "1234", "Adel");

        // Create courier
        response = createCourier(json);

        // Validate error response for missing login
        validateErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Step("Check creation of courier without password")
    public void createCourierWithoutPassword() {
        // Create JSON for courier without password
        json = createCourierJson("misterLogin" + randomIntInRange, null, "Adel");

        // Create courier
        response = createCourier(json);

        // Validate error response for missing password
        validateErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @After
    @Step("Clean up: Delete the created courier")
    public void shutDown() {
        String statusCode = String.valueOf(response.statusCode());
        if (statusCode.startsWith("2") || statusCode.equals("409")) {
            // Prepare data for login request
            json.remove("firstName");

            // Log in to the created courier account
            response = loginCourier(json);

            // Extract courier ID and delete the courier
            String courierId = extractCourierId(response);
            deleteCourier(courierId);
        }
    }

    @Step("Create JSON for courier with different keys")
    public JsonObject createCourierJson(String login, String password, String firstName) {
        JsonObject json = new JsonObject();
        if (login != null) json.addProperty("login", login);
        if (password != null) json.addProperty("password", password);
        if (firstName != null) json.addProperty("firstName", firstName);
        return json;
    }

    @Step("Send POST request to create a courier")
    public Response createCourier(JsonObject json) {
        return given()
                .header("Content-type", "application/json")
                .body(json.toString())
                .when()
                .post("/api/v1/courier");
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

    @Step("Send POST request to log in courier")
    public Response loginCourier(JsonObject json) {
        return given()
                .header("Content-type", "application/json")
                .body(json.toString())
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Extract courier ID from login response")
    public String extractCourierId(Response response) {
        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getString("id");
    }

    @Step("Delete courier with ID: {courierId}")
    public void deleteCourier(String courierId) {
        JsonObject deleteJson = new JsonObject();
        deleteJson.addProperty("id", courierId);

        given()
                .header("Content-type", "application/json")
                .body(deleteJson.toString())
                .when()
                .delete("/api/v1/courier/:id");
    }
}