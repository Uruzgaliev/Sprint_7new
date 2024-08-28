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


public class CourierCreateTest {

    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    // подумать, как сделать так, чтобы удалялся курьер при повторном тесте
    @After
    public void cleanUp() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Создание курьера со всеми заполненными полями")
    public void courierCanBeCreated() {
        Courier courier = new Courier("Simon865", "1234567891", "saske321");
        ValidatableResponse createResponse = courierClient.create(courier);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(createStatusCode, 201);
        boolean created = createResponse.extract().path("ok");
        assertTrue(created);

        // Проверяем, что можно войти в систему под созданным курьером
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 200);
        courierId = loginResponse.extract().path("id");
        assertNotEquals(courierId, 0);
    }

    @Test
    @DisplayName("Создание курьера с данными уже существующими в БД")
    @Description("Создание второго курьера со существующими данными - ожидание 409 ошибки")
    public void cannotCreateDuplicateCouriers() {

        Courier courier = new Courier("Simon865", "1234567891", "saske321");
        // Создаем первого курьера
        ValidatableResponse createResponse1 = courierClient.create(courier);
        int createStatusCode1 = createResponse1.extract().statusCode();
        assertEquals(createStatusCode1, 201);
        boolean created1 = createResponse1.extract().path("ok");
        assertTrue(created1);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(loginStatusCode, 200);
        courierId = loginResponse.extract().path("id");
        assertNotEquals(courierId, 0);

        // Создаем второго курьера с теми же данными
        Courier duplicateCourier = new Courier("Simon865", "1234567891", "saske321");
        ValidatableResponse createResponse2 = courierClient.create(duplicateCourier);
        int createStatusCode2 = createResponse2.extract().statusCode();
        assertEquals(createStatusCode2, 409); // ожидаем код ошибки 409
        String errorMessage = createResponse2.extract().path("message");
        assertEquals(errorMessage, "Этот логин уже используется.");// Проверяем сообщение об ошибке
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Создание курьера без логина - курьер не создается, ожидание 400 ошибки")
    public void courierNotCreatedWithoutLogin() {
        Courier courier = new Courier("", "1234567891", "saske321");

        ValidatableResponse createResponse = courierClient.create(courier);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(createStatusCode, 400);
        String errorMessage = createResponse.extract().path("message");
        assertEquals(errorMessage, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Создание курьера без пароля - курьер не создается, ожидание 400 ошибки")
    public void courierNotCreatedWithoutPassword() {
        Courier courier = new Courier("Simon865", "", "saske321");

        ValidatableResponse createResponse = courierClient.create(courier);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(createStatusCode, 400);
        String errorMessage = createResponse.extract().path("message");
        assertEquals(errorMessage, "Недостаточно данных для создания учетной записи");
    }

}
