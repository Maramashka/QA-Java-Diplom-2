package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.models.User;
import stellarburgers.models.UserCreds;

import static io.restassured.RestAssured.given;

public class UserClient extends Spec {

    public static final String USER_CREATE = "/api/auth/register";
    public static final String USER_LOGIN = "/api/auth/login";
    public static final String USER_UPDATE = "/api/auth/user";
    public static final String USER_DELETE = "/api/auth/user";

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(USER_CREATE)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(User user) {

        UserCreds creds = UserCreds.credsFromUser(user);

        return given()
                .spec(requestSpecification())
                .and()
                .body(creds)
                .when()
                .post(USER_LOGIN)
                .then();
    }

    @Step("Обновление данных пользователя без авторизации")
    public ValidatableResponse updateUserWithoutAuthorization(User user) {

        UserCreds creds = UserCreds.credsFromUser(user);

        return given()
                .spec(requestSpecification())
                .and()
                .body(creds)
                .when()
                .patch(USER_UPDATE)
                .then();
    }

    @Step("Обновление данных пользователя после авторизации")
    public ValidatableResponse updateUserAfterAuthorization(User user, String accessToken) {

        UserCreds creds = UserCreds.credsFromUser(user);

        return given()
                .spec(requestSpecification())
                .and()
                .header("authorization", accessToken)
                .body(creds)
                .when()
                .patch(USER_UPDATE)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse delete(String accessToken) {

        return given()
                .spec(requestSpecification())
                .and()
                .header("authorization", accessToken)
                .when()
                .delete(USER_DELETE)
                .then();
    }
}
