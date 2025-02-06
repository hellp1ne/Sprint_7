import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class TestCreateOrder {

    private String[] color;

    public TestCreateOrder(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK", "GREY"}}, // Both colors
                {new String[]{"BLACK"}}, // Black color
                {new String[]{"GREY"}}, // Grey color
                {new String[]{}} // Empty array
        });
    }

    private JsonObject json;
    private Response response;

    @Before
    @Step("Set up test environment and create an order")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // Create an order with the specified colors
        Order order = createOrderWithColors(color);

        // Convert the order to JSON
        String orderJson = convertOrderToJson(order);

        // Create the order via API
        response = createOrder(orderJson);
    }

    @Test
    @Step("Check response body and status code")
    public void checkBodyResponse() {
        validateOrderCreationResponse(response);
    }

    @After
    @Step("Clean up: Cancel the created order")
    public void shutDown() {
        // Extract the track number from the response
        int trackNumber = extractTrackNumber(response);

        // Cancel the order
        cancelOrder(trackNumber);
    }

    @Step("Create an order with colors: {colors}")
    public Order createOrderWithColors(String[] colors) {
        List<String> colorList = Arrays.asList(colors);
        return new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colorList
        );
    }

    @Step("Convert order to JSON")
    public String convertOrderToJson(Order order) {
        Gson gson = new Gson();
        return gson.toJson(order);
    }

    @Step("Send POST request to create an order")
    public Response createOrder(String orderJson) {
        return given()
                .header("Content-type", "application/json")
                .body(orderJson)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Validate order creation response")
    public void validateOrderCreationResponse(Response response) {
        response.then()
                .statusCode(201)
                .and()
                .assertThat()
                .body("track", notNullValue());
    }

    @Step("Extract track number from response")
    public int extractTrackNumber(Response response) {
        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getInt("track");
    }

    @Step("Cancel order with track number: {trackNumber}")
    public void cancelOrder(int trackNumber) {
        String jsonString = "{\"track\":\"" + trackNumber + "\"}";
        JsonElement rootElement = JsonParser.parseString(jsonString);
        json = rootElement.getAsJsonObject();

        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .put("/api/v1/orders/cancel");
    }
}