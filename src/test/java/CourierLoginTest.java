import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import courier.Courier;
import courier.CourierClient;
import courier.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class CourierLoginTest {
    private CourierClient courierClient;
    private int courierId;
    Courier courier;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = new Courier("Simon865", "1234567891", "saske321");
        courierClient.create(courier);

    }

    // Удаление курьера по ID
    @After
    public void cleanUp() {
        courierClient.delete(courierId);
    }

    // Тест на корректную авторизацию курьера
    @Test
    @DisplayName("Авторизация курьера")
    @Description("Авторизация курьера - авторизация проходит, ожидание 200 кода")
    public void TestCourierAuthorization() {
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 200);
        courierId = loginResponse.extract().path("id");
        assertNotEquals(courierId, 0);
    }

    // Тест на авторизацию курьера без логина
    @Test
    @DisplayName("Авторизация курьера без логина")
    @Description("Авторизация курьера без логина - авторизация не проходит, ожидание 400 ошибки")
    public void TestCourierNotAuthorizationWithoutLogin() {
        courier.setLogin("");
        courier.setPassword("1234567891");
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        // Проверка статуса кода ответа
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 400);
        // Проверка сообщения об ошибке
        String errorMessage = loginResponse.extract().path("message");
        assertEquals(errorMessage, "Недостаточно данных для входа");
    }

    // Тест на авторизацию курьера без пароля
    @Test
    @DisplayName("Авторизация курьера без пароля")
    @Description("Авторизация курьера без пароля - авторизация не проходит, ожидание 400 ошибки")
    public void TestCourierNotAuthorizationWithoutPassword() {
        courier.setLogin("Simon865");
        courier.setPassword("");

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));

        // Проверка статуса кода ответа
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 400);

        // Проверка сообщения об ошибке
        String errorMessage = loginResponse.extract().path("message");
        assertEquals(errorMessage, "Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Авторизация курьера без логина и пароля")
    @Description("Авторизация курьера без логина и пароля - авторизация не проходит, ожидание 400 ошибки")
    public void TestCourierNotAuthorizationWithoutLoginAndPassword() {
        courier.setLogin("");
        courier.setPassword("");

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));

        // Проверка статуса кода ответа
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 400);

        // Проверка сообщения об ошибке
        String errorMessage = loginResponse.extract().path("message");
        assertEquals(errorMessage, "Недостаточно данных для входа");
    }


    @Test
    @DisplayName("Авторизация курьера с несуществующим логином")
    @Description("Авторизация курьера с несуществующим логином - авторизация не проходит, ожидание 404 ошибки")
    public void TestCourierNotAuthorizationWithMistakeLogin() {
        courier.setLogin("Simon86Simon");
        courier.setPassword("1234567891");

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));

        // Проверка статуса кода ответа
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 404);

        // Проверка сообщения об ошибке
        String errorMessage = loginResponse.extract().path("message");
        assertEquals(errorMessage, "Учетная запись не найдена");
    }

    // Тест на авторизацию курьера с несуществующим паролем
    @Test
    @DisplayName("Авторизация курьера с несуществующим паролем")
    @Description("Авторизация курьера с несуществующим паролем - авторизация не проходит, ожидание 404 ошибки")
    public void TestCourierNotAuthorizationWithMistakePassword() {
        courier.setLogin("Simon865");
        courier.setPassword("123456789");

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));

        // Проверка статуса кода ответа
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 404);

        // Проверка сообщения об ошибке
        String errorMessage = loginResponse.extract().path("message");
        assertEquals(errorMessage, "Учетная запись не найдена");
    }

}
