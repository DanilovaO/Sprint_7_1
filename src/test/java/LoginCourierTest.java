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



public class LoginCourierTest {
    private Courier courier;
    Gson gson = new Gson();
    private final String baseURI = "https://qa-scooter.praktikum-services.ru";
    private final String courierLoginEndpoint = "/api/v1/courier/login/";
    private final String courierEndpoint = "/api/v1/courier/";

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURI;
        String login = RandomStringUtils.randomAlphanumeric(7);
        courier = new Courier(login, "autotest123", "autotest789");
    }

    @Step("Создаём курьера")
    public Response createCourier() {
        return given().header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierEndpoint);
    }

    @After
    public void deleteCourier() {
        if (courier.getId() == null) {
            Response login = loginCourier();
            if (login == null) {
                return;
            }
        }
        given().header("Content-type", "application/json")
                .body(gson.toJson(courier)).delete("/api/v1/courier/" + courier.getId())
                .then().statusCode(200).and().assertThat().body("ok", equalTo(true));
    }

    @Step("Логин курьера в системе")
    public Response loginCourier() {
        Response responseLogin = given().header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint);
        System.out.println(responseLogin.statusCode());
        if (responseLogin.statusCode() != 200) {
            return null;
        }
        courier.setId(responseLogin.path("id").toString());
        return responseLogin;
    }

    @Test
    @DisplayName("Авторизация курьера")
    public void checkLoginCourier() {
        createCourier();
        Response response = given().header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint);
        response.then().statusCode(200).and().assertThat().body("id", notNullValue());
        courier.setId(response.path("id").toString());
    }

    @Test
    @DisplayName("Авторизация курьера без заполненного обязательного поля: логин")
    public void checkCourierAuthorisationWithEmptyLogin() {
        createCourier();
        String login = courier.getLogin();
        courier.setLogin("");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint)
                .then().statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
        courier.setLogin(login);
    }
    @Test
    @DisplayName("Авторизация курьера без заполненного обязательного поля: пароль")
    public void checkCourierAuthorisationWithEmptyPassword() {
        createCourier();
        String password = courier.getPassword();
        courier.setPassword("");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint)
                .then().statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
        courier.setPassword(password);
    }
    @Test
    @DisplayName("Авторизация курьера с неправильным логином")
    public void checkCourierAuthorisationWithWrongLogin() {
        createCourier();
        String login = courier.getLogin();
        courier.setLogin("WrongLogin111");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint)
                .then().statusCode(404)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
        courier.setLogin(login);
    }
    @Test
    @DisplayName("Авторизация курьера с неправильным паролем")
    public void checkCourierAuthorisationWithWrongPassword() {
        createCourier();
        String password = courier.getPassword();
        courier.setPassword("111111");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint)
                .then().statusCode(404)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
        courier.setPassword(password);
    }
    @Test
    @DisplayName("Авторизация курьера без обязательного поля: логин")
    public void checkCourierAuthorisationWithoutLoginField() {
        createCourier();
        String login = courier.getLogin();
        courier.setLogin(null);
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint)
                .then().statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
        courier.setLogin(login);
    }
    @Test
    @DisplayName("Авторизация курьера без обязательного поля: пароль")
    public void checkCourierAuthorisationWithoutPasswordField() {
        createCourier();
        String password = courier.getPassword();
        courier.setPassword(null);
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint)
                .then().statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
        courier.setPassword(password);
    }
    @Test
    @DisplayName("Авторизация несуществующего курьера")
    public void checkCourierAuthorisationWithWrongLoginAndPassword() {
        createCourier();
        String login = courier.getLogin();
        courier.setLogin("WrongLogin111");
        String password = courier.getPassword();
        courier.setPassword("111111");
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierLoginEndpoint)
                .then().statusCode(404)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
        courier.setLogin(login);
        courier.setPassword(password);
    }
}



