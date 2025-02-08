import api.OrderApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class TestGetOrders {
    private OrderApiClient orderApiClient = new OrderApiClient();

    @Test
    public void checkColorChoiceTest() {
        // Validate that the response contains a list of orders
        validateOrdersResponse(orderApiClient.getOrdersAtStation());
    }

    @Step("Validate that the response contains a list of orders")
    public void validateOrdersResponse(Response response) {
        response.then()
                .assertThat()
                .body("orders", is(instanceOf(java.util.List.class)))
                .and()
                .statusCode(200);
    }
}