import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;
import steps.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.notNullValue;

public class UserCreateTests extends BaseTest {
    private User user;
    private UserSteps userSteps;

    @Before
    public void startUp() {
        user = new User();
        userSteps = new UserSteps();
        user.setName(RandomStringUtils.randomAlphabetic(8));
        user.setEmail(RandomStringUtils.randomAlphabetic(8) + "@yandex.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(8));
    }

    @After
    public void tearDown() {
        String accessToken = UserSteps.getAccessTokenRequest(user);
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание уникального пользователя")
    @Description("Успешное создание уникального пользователя с валидными данными")
    public void createUniqueUserTest() {
        userSteps
                .createUser(user)
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Неуспешное создание уже зарегистрированного пользователя")
    @Description("Неуспешное создание пользователя с данными уже созданного пользователя")
    public void canNotCreateNotUniqueUserTest() {
        userSteps.createUser(user);
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Неуспешное создание пользователя без 'email'")
    @Description("Пользователь без поля 'email' не создаётся")
    public void canNotCreateUserWithoutEmailTest() {
        user.setEmail("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Неуспешное создание пользователя без 'password'")
    @Description("Пользователь без поля 'password' не создаётся")
    public void canNotCreateUserWithoutPasswordTest() {
        user.setPassword("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Неуспешное создание пользователя без 'name'")
    @Description("Пользователь без поля 'name' не создаётся")
    public void canNotCreateUserWithoutNameTest() {
        user.setName("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}