import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import orders.OrderApi;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GetOrdersTest {

    private OrderApi orderApi;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    // Получение списка заказов
    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что в тело ответа возвращается список заказов")
    public void testGetOrder() {
        ValidatableResponse getResponse = orderApi.getOrdersList();
        int getStatusCode = getResponse.extract().statusCode();
        assertEquals(getStatusCode, 200);
        List<Object> orders = getResponse.extract().path("orders");
        assertNotEquals(orders.size(), 0);
    }
}
