import api.Courier;
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
import static org.apache.http.HttpStatus.*;



public class CreateCourierTest {
    private Courier courier;

    private CourierApi courierApi = new CourierApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = courierApi.baseURI;
        String login  = RandomStringUtils.randomAlphanumeric(7);
        courier = new Courier(login, "autotest123", "autotest789");
    }

    @After
    public void deleteCourier() {
        if (courier.getId() == null) {
            Response login = courierApi.loginCourier(courier);
            if (login == null){
                return;
            }
            courier.setId(login.path("id").toString());
        }
        given().header("Content-type", "application/json")
                .body(courier).delete("/api/v1/courier/" + courier.getId())
                .then().statusCode(SC_OK).and().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при успешном создании курьера")
    public void checkStatusCodeSuccessCreate() {
        courierApi.createCourier(courier).then().statusCode(SC_CREATED).and().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при попытке создать уже существующий аккаунт курьера")
    public void checkDuplicateCourierCreateStatus() {
        courierApi.createCourier(courier);
        courierApi.createCourier(courier).then().statusCode(SC_CONFLICT).and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: логин")
    public void checkCreateCourierWithEmptyLogin() {
        String login = courier.getLogin();
        courier.setLogin("");
        courierApi.createCourier(courier).then().statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        courier.setLogin(login);
    }

    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: пароль")
    public void checkCreateCourierWithEmptyPassword() {
        courier.setPassword("");
        courierApi.createCourier(courier).then().statusCode(SC_BAD_REQUEST).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: имя")
    public void checkCreateCourierWithEmptyFirstName() {
        courier.setFirstName("");
        courierApi.createCourier(courier).then().statusCode(SC_CREATED ).and().assertThat().body("ok", equalTo(true));
    }
    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: логин")
    public void checkCreateCourierWithoutLoginField() {
        courier.setLogin(null);
        courierApi.createCourier(courier).then().statusCode(SC_BAD_REQUEST).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: пароль")
    public void checkCreateCourierWithoutPasswordField() {
        courier.setPassword(null);
        courierApi.createCourier(courier).then().statusCode(SC_BAD_REQUEST).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: имя")
    public void checkCreateCourierWithoutFirstNameField() {
        courier.setFirstName(null);
        courierApi.createCourier(courier).then().statusCode(SC_CREATED).and().assertThat().body("ok", equalTo(true));
    }
}
