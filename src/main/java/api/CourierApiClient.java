package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import json.CreateCourierRequest;
import json.DeleteCourierRequest;
import json.LoginCourierRequest;
import uri.RequestSpec;

import static io.restassured.RestAssured.given;

public class CourierApiClient {

    // Endpoints
    final String POST_CREATE_COURIER = "/api/v1/courier";
    final String POST_LOGIN_COURIER = "/api/v1/courier/login";
    final String DELETE_COURIER = "/api/v1/courier/:id";

    @Step("Send POST request to create a courier")
    public Response createCourier(CreateCourierRequest courierRequest) {
        return given()
                .spec(RequestSpec.requestSpec)
                .body(courierRequest)
                .when()
                .post(POST_CREATE_COURIER);
    }

    @Step("Send POST request to log in courier")
    public Response loginCourier(LoginCourierRequest loginRequest) {
        return given()
                .spec(RequestSpec.requestSpec)
                .body(loginRequest)
                .when()
                .post(POST_LOGIN_COURIER);
    }

    @Step("Delete courier with ID: {courierId}")
    public void deleteCourier(String courierId) {
        DeleteCourierRequest deleteRequest = new DeleteCourierRequest(courierId);
        given()
                .spec(RequestSpec.requestSpec)
                .body(deleteRequest)
                .when()
                .delete(DELETE_COURIER);
    }
}