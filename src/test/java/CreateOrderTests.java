import config.Constants;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.Order;
import pojo.User;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Arrays;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTests extends BaseTest {
    private User user;
    private UserSteps userSteps;
    private Order order;
    private OrderSteps orderSteps;

    @Before
    public void startUp() {
        user = new User();
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();

        user.setName(RandomStringUtils.randomAlphabetic(8));
        user.setEmail(RandomStringUtils.randomAlphabetic(8) + "@yandex.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(8));
        userSteps.createUser(user);
    }

    @After
    public void tearDown() {
        String accessToken = UserSteps.getAccessTokenRequest(user);
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание заказа с авторизацией и ингредиентами")
    @Description("Успешное создание заказа с валидными данными и авторизацией")
    public void createOrderWithAccessTokenTest() {
        order = new Order(Arrays.asList(Constants.BUN, Constants.FILLINGS));
        orderSteps
                .createOrder(order, UserSteps.getAccessTokenRequest(user))
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Успешное создание заказа с ингредиентами без авторизации")
    @Description("Успешное создание заказа с валидными данными без авторизации")
    public void createOrderWithoutAccessTokenTest() {
        order = new Order(Arrays.asList(Constants.BUN, Constants.FILLINGS));
        orderSteps
                .createOrder(order, "")
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Невозможность создания заказа без ингредиентов")
    @Description("Нельзя создать заказ без ингредиентов")
    public void canNotCreateOrderWithoutIngredientsTest() {
        order = new Order();
        orderSteps
                .createOrder(order, UserSteps.getAccessTokenRequest(user))
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Невозможность создания заказа с невалидным хешом ингредиентов")
    @Description("Нельзя создать заказ с невалидным хешом ингредиентов")
    public void canNotCreateOrderWithInvalidIngredientsTest() {
        order = new Order(Arrays.asList("11515afaf", "ggg2g645uhj"));
        orderSteps
                .createOrder(order, UserSteps.getAccessTokenRequest(user))
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}