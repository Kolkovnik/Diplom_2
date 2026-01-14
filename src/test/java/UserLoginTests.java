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

public class UserLoginTests extends BaseTest {
    private User user;
    private UserSteps userSteps;

    @Before
    public void startUp() {
        user = new User();
        userSteps = new UserSteps();
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
    @DisplayName("Успешное логирование под существующим пользователем")
    @Description("Возможность логирования под существующим пользователем")
    public void loginWithCorrectUser() {
        UserSteps
                .loginUser(user)
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Невозможность логирования с некорректным 'email'")
    @Description("Невозможность логирования с некорректным полем 'email'")
    public void canNotLoginUserWithINcorrectEmailTest() {
        user.setEmail("aaaaaa");
        UserSteps
                .loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Невозможность логирования с некорректным 'password'")
    @Description("Невозможность логирования с некорректным полем 'password'")
    public void canNotLoginUserWithINcorrectPasswordTest() {
        user.setPassword("aaaaaa");
        UserSteps
                .loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}