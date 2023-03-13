import api.Courier;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.Reader;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.apache.http.HttpStatus.*;




public class LoginCourierTest {
    private Courier courier;
    Gson gson = new Gson();
    private CourierApi courierApi = new CourierApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = courierApi.baseURI;
        String login = RandomStringUtils.randomAlphanumeric(7);
        courier = new Courier(login, "autotest123", "autotest789");
    }


    @After
    public void deleteCourier() {
        if (courier.getId() == null) {
            Response login = courierApi.loginCourier(courier);
            if (login == null) {
                return;
            }
        }
        given().header("Content-type", "application/json")
                .body(gson.toJson(courier)).delete("/api/v1/courier/" + courier.getId())
                .then().statusCode(SC_OK).and().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Авторизация курьера")
    public void checkLoginCourier() {
        courierApi.createCourier(courier);
        Response response = given().header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint);
        response.then().statusCode(SC_OK).and().assertThat().body("id", notNullValue());
        courier.setId(response.path("id").toString());
    }

    @Test
    @DisplayName("Авторизация курьера без заполненного обязательного поля: логин")
    public void checkCourierAuthorisationWithEmptyLogin() {
        courierApi.createCourier(courier);
        courier.setLogin("");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint)
                .then().statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @Test
    @DisplayName("Авторизация курьера без заполненного обязательного поля: пароль")
    public void checkCourierAuthorisationWithEmptyPassword() {
        courierApi.createCourier(courier);
        courier.setPassword("");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint)
                .then().statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @Test
    @DisplayName("Авторизация курьера с неправильным логином")
    public void checkCourierAuthorisationWithWrongLogin() {
        courierApi.createCourier(courier);
        courier.setLogin("WrongLogin111");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint)
                .then().statusCode(SC_NOT_FOUND)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }
    @Test
    @DisplayName("Авторизация курьера с неправильным паролем")
    public void checkCourierAuthorisationWithWrongPassword() {
        courierApi.createCourier(courier);
        courier.setPassword("111111");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint)
                .then().statusCode(SC_NOT_FOUND)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }
    @Test
    @DisplayName("Авторизация курьера без обязательного поля: логин")
    public void checkCourierAuthorisationWithoutLoginField() {
        courierApi.createCourier(courier);
        courier.setLogin(null);
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint)
                .then().statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @Test
    @DisplayName("Авторизация курьера без обязательного поля: пароль")
    public void checkCourierAuthorisationWithoutPasswordField() {
        courierApi.createCourier(courier);
        courier.setPassword(null);
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint)
                .then().statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @Test
    @DisplayName("Авторизация несуществующего курьера")
    public void checkCourierAuthorisationWithWrongLoginAndPassword() {
        courierApi.createCourier(courier);
        courier.setLogin("WrongLogin111");
        courier.setPassword("111111");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierApi.courierLoginEndpoint)
                .then().statusCode(SC_NOT_FOUND)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }
}



