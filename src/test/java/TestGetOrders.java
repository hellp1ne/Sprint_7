import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestGetOrders {

    @Before
    @Step("Set up base URI for the API")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @Step("Check color choice and validate response structure")
    public void checkColorChoice() {
        // Get all orders at the first metro station
        Response response = getOrdersAtStation("1");

        // Validate that the response contains a list of orders
        validateOrdersResponse(response);
    }

    @Step("Send GET request to retrieve orders at a specific metro station")
    public Response getOrdersAtStation(String stationId) {
        return given()
                .queryParam("nearestStation", "[\"" + stationId + "\"]")
                .when()
                .get("/api/v1/orders");
    }

    @Step("Validate that the response contains a list of orders")
    public void validateOrdersResponse(Response response) {
        response.then()
                .assertThat()
                .body("orders", is(instanceOf(java.util.List.class)));
    }
}