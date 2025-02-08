package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import json.CancelOrderRequest;
import json.CreateOrderRequest;
import uri.RequestSpec;

import static io.restassured.RestAssured.given;

public class OrderApiClient {

    // Endpoints
    final String POST_CREATE_ORDER = "/api/v1/orders";
    final String PUT_CANCEL_ORDER = "/api/v1/orders/cancel";
    final String GET_ORDERS = "/api/v1/orders";

    @Step("Send POST request to create an order")
    public Response createOrder(CreateOrderRequest orderRequest) {
        return given()
                .spec(RequestSpec.requestSpec)
                .header("Content-type", "application/json")
                .body(orderRequest)
                .when()
                .post(POST_CREATE_ORDER);
    }

    @Step("Cancel order with track number: {trackNumber}")
    public Response cancelOrder(CancelOrderRequest cancelRequest) {
        return given()
                .spec(RequestSpec.requestSpec)
                .body(cancelRequest)
                .when()
                .put(PUT_CANCEL_ORDER);
    }

    @Step("Send GET request to retrieve orders at a specific metro station")
    public Response getOrdersAtStation() {
        return given()
                .spec(RequestSpec.requestSpec)
                .when()
                .get(GET_ORDERS);
    }
}