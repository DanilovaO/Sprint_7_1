import api.Courier;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import static org.apache.http.HttpStatus.*;

import static io.restassured.RestAssured.given;

public class CourierApi {

    public final String baseURI = "https://qa-scooter.praktikum-services.ru";
    public final String courierEndpoint = "/api/v1/courier/";
    public final String courierLoginEndpoint = "/api/v1/courier/login/";



    @Step("Создаём курьера")
    public Response createCourier(Courier courier ) {
        return given().header("Content-type", "application/json")
                .body(courier)
                .post(courierEndpoint);
    }

    @Step("Логин курьера в системе")
    public Response loginCourier(Courier courier) {
        Response responseLogin = given().header("Content-type", "application/json")
                .body(courier)
                .post(courierLoginEndpoint);
        if (responseLogin.statusCode() != SC_OK) {
            return null;
        }
        return responseLogin;
    }
}
