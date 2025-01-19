package user;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.models.User;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static stellarburgers.generators.UserGenerator.randomUser;

public class UserUpdateTests {

    private User user;
    private User newUser;
    private UserClient userClient;
    private String accessToken;

    ValidatableResponse loginResponse;
    ValidatableResponse response;

    @Before
    public void setUp() {
        user = randomUser();
        userClient = new UserClient();
        userClient.create(user);
    }


    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Изменение email без авторизации")
    public void userUpdateEmailWithoutAuthorizationErrorTest() {
        user.setEmail(randomUser().getEmail());

        response = userClient.updateUserWithoutAuthorization(user);

        response.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @Description("Изменение пароля без авторизации")
    public void userUpdatePasswordWithoutAuthorizationErrorTest() {
        user.setPassword(randomUser().getPassword());

        response = userClient.updateUserWithoutAuthorization(user);

        response.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @Description("Изменение имени без авторизации")
    public void userUpdateNameWithoutAuthorizationErrorTest() {
        user.setName(randomUser().getName());

        response = userClient.updateUserWithoutAuthorization(user);

        response.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }


    @Test
    @Description("Изменение email после авторизации")
    public void userUpdateEmailAfterAuthorizationTest() {
        loginResponse = userClient.login(user);
        accessToken = loginResponse.extract().path("accessToken");

        user.setEmail(randomUser().getEmail());

        response = userClient.updateUserAfterAuthorization(user, accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(user.getEmail().toLowerCase().toString()));
    }

    @Test
    @Description("Изменение email после авторизации, на уже используемую email")
    public void userUpdateEmailAlreadyUsedAfterAuthorizationErrorTest() {
        loginResponse = userClient.login(user);
        accessToken = loginResponse.extract().path("accessToken");
        newUser = randomUser();
        userClient.create(newUser);
        user.setEmail(newUser.getEmail());

        response = userClient.updateUserAfterAuthorization(user, accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User with such email already exists"));
    }


    @Test
    @Description("Изменение пароля после авторизации")
    public void userUpdatePasswordAfterAuthorizationTest() {
        loginResponse = userClient.login(user);
        accessToken = loginResponse.extract().path("accessToken");
        user.setPassword(randomUser().getPassword());

        response = userClient.updateUserAfterAuthorization(user, accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));

    }

    @Test
    @Description("Изменение имени после авторизации")
    public void userUpdateNameAfterAuthorizationTest() {
        loginResponse = userClient.login(user);
        accessToken = loginResponse.extract().path("accessToken");
        user.setName(randomUser().getName());

        response = userClient.updateUserAfterAuthorization(user, accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.name", is(user.getName().toString()));
    }
}