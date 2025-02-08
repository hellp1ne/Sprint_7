import api.OrderApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import json.CancelOrderRequest;
import json.CreateOrderRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class TestCreateOrder {

    private String[] color;

    public TestCreateOrder(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Colors: [{0}, {1}]")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK", "GREY"}}, // Both colors
                {new String[]{"BLACK"}}, // Black color
                {new String[]{"GREY"}}, // Grey color
                {new String[]{}} // Empty array
        });
    }

    private Response response;
    private OrderApiClient orderApiClient = new OrderApiClient();

    @Test
    public void checkBodyResponseTest() {
        // Create an order with the specified colors
        CreateOrderRequest order = createOrderWithColors(color);

        // Create the order via API
        response = orderApiClient.createOrder(order);
        validateOrderCreationResponse(response);
    }

    @After
    public void shutDown() {
        // Extract the track number from the response
        int trackNumber = extractTrackNumber(response);

        // Cancel the order
        cancelOrder(trackNumber);
    }

    @Step("Create an order with colors: {colors}")
    public CreateOrderRequest createOrderWithColors(String[] colors) {
        List<String> colorList = Arrays.asList(colors);
        return new CreateOrderRequest(
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
        return response.jsonPath().getInt("track");
    }

    @Step("Cancel order with track number: {trackNumber}")
    public void cancelOrder(int trackNumber) {
        // Create a objectsJSON.CancelOrderRequest object
        CancelOrderRequest cancelOrderRequest = new CancelOrderRequest(trackNumber);

        // Cancel the order via API
        orderApiClient.cancelOrder(cancelOrderRequest);
    }
}