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
import static org.hamcrest.Matchers.equalTo;



public class CreateCourierTest {
    private Courier courier;
    Gson gson = new Gson();
    private final String baseURI = "https://qa-scooter.praktikum-services.ru";
    private final String courierLoginEndpoint = "/api/v1/courier/login/";
    private final String courierEndpoint = "/api/v1/courier/";

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURI;
        String login  = RandomStringUtils.randomAlphanumeric(7);
        courier = new Courier(login, "autotest123", "autotest789");
    }
    @Step("Создаём курьера")
    public Response createCourier() {
        return given().header("Content-type", "application/json")
                .body(gson.toJson(courier))
                .post(courierEndpoint);
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

    @After
    public void deleteCourier() {
        if (courier.getId() == null) {
            Response login = loginCourier();
            if (login == null){
                return;
            }
        }
        given().header("Content-type", "application/json")
                .body(gson.toJson(courier)).delete("/api/v1/courier/" + courier.getId())
                .then().statusCode(200).and().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при успешном создании курьера")
    public void checkStatusCodeSuccessCreate() {
        createCourier().then().statusCode(201).and().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при попытке создать уже существующий аккаунт курьера")
    public void checkDuplicateCourierCreateStatus() {
        createCourier();
        createCourier().then().statusCode(409).and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: логин")
    public void checkCreateCourierWithEmptyLogin() {
        String login = courier.getLogin();
        courier.setLogin("");
        createCourier().then().statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        courier.setLogin(login);
    }

    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: пароль")
    public void checkCreateCourierWithEmptyPassword() {
       // String password = courier.getPassword();
        courier.setPassword("");
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
      //  courier.setPassword(password);
    }

    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: имя")
    public void checkCreateCourierWithEmptyFirstName() {
        courier.setFirstName("");
        createCourier().then().statusCode(201 ).and().assertThat().body("ok", equalTo(true));
    }
    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: логин")
    public void checkCreateCourierWithoutLoginField() {
       // String login = courier.getLogin();
        courier.setLogin(null);
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
     //   courier.setLogin(login);
    }

    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: пароль")
    public void checkCreateCourierWithoutPasswordField() {
        //String password = courier.getPassword();
        courier.setPassword(null);
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
      //  courier.setPassword(password);
    }

    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: имя")
    public void checkCreateCourierWithoutFirstNameField() {
        courier.setFirstName(null);
        createCourier().then().statusCode(201).and().assertThat().body("ok", equalTo(true));
    }
}
