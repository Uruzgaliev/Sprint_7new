import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import orders.Order;
import orders.OrderApi;
import orders.OrderCancel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private int track;
    private final List<String> color;
    OrderApi orderApi;
    Order order;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] testData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()},
        };
    }

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        order = new Order("Семен", "Семенов", "улица Семенова, 10", "4", "+7 915 322 23 34", 5, "2024-08-12", "быстрее", color);
    }

    // Создаем заказ
    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверка, что можно создать заказ с переданными наборами цветов")
    public void testOrderCreation () {
        ValidatableResponse createResponse = orderApi.createOrder(order);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(createStatusCode, 201);
        int track = createResponse.extract().path("track");
        assertNotEquals(track, 0);
        this.track = track;
    }

    @After
    public void cancelOrder() {
        // Отменяем заказ по track
        if (track != 0) {
            OrderCancel cancel = new OrderCancel(track);
            orderApi.cancelOrder(cancel);
        }
    }
}
