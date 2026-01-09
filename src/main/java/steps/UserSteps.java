package steps;

import config.Constants;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import pojo.User;

import static io.restassured.RestAssured.given;

public class UserSteps {

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(Constants.CREATE_USER)
                .then();
    }

    @Step("Залогинить пользователя")
    public static ValidatableResponse loginUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(Constants.LOGIN_USER)
                .then();
    }

    @Step("Удалить пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(Constants.DELETE_USER)
                .then();
    }

    @Step("Запрос на получение accessToken")
    public static String getAccessTokenRequest(User user) {
        return UserSteps
                .loginUser(user)
                .extract()
                .body()
                .path("accessToken");
    }
}