package user;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.models.User;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;
import static stellarburgers.generators.UserGenerator.randomUser;

public class UserLoginTests {

    private User user;
    private User newUser;
    private UserClient userClient;
    private String accessToken;

    ValidatableResponse response;

    @Before
    public void setUp() {
        user = randomUser();
        userClient = new UserClient();
        userClient.create(user);
        response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Авторизация под существующим пользователем")
    public void userLoginExistingUserTest() {
        response = userClient.login(user);

        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", isA(String.class));
    }

    @Test
    @Description("Авторизация пользователя при некорректном поле email")
    public void userLoginWithIncorrectEmailErrorTest() {
        newUser = new User(randomUser().getEmail(), user.getPassword(), user.getName());

        response = userClient.login(newUser);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @Description("Авторизация пользователя при незаполненном поле email")
    public void userLoginWithEmptyEmailErrorTest() {
        user = new User(null, randomUser().getPassword(), randomUser().getName());

        response = userClient.login(user);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @Description("Авторизация пользователя при некорректном поле password")
    public void userLoginWithIncorrectPasswordErrorTest() {
        newUser = new User(user.getEmail(), randomUser().getPassword(), user.getName());

        response = userClient.login(newUser);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @Description("Авторизация пользователя при незаполненном поле password)")
    public void userLoginWithEmptyPasswordErrorTest() {
        user = new User(randomUser().getEmail(), null, randomUser().getName());

        response = userClient.login(user);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));

    }
}
