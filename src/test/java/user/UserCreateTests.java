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


public class UserCreateTests {

    private User user;
    private UserClient userClient;
    String accessToken;

    ValidatableResponse response;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание уникального пользователя")
    public void userCreateTest() {

        user = randomUser();

        response = userClient.create(user);
        accessToken = response.extract().path("accessToken");

        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", isA(String.class));

    }

    @Test
    @Description("Создание пользователя, который уже зарегистрирован")
    public void userCreateAlreadyRegisteredErrorTest() {

        user = randomUser();
        userClient.create(user);
        User userCopy = user;

        response = userClient.create(userCopy);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @Description("Создание пользователя при незаполненном обязательном поле email (логин)")
    public void userCreateWithoutEmailErrorTest() {

        user = new User(null, randomUser().getPassword(), randomUser().getName());
        userClient = new UserClient();

        response = userClient.create(user);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));

    }

    @Test
    @Description("Создание пользователя при незаполненном обязательном поле password")
    public void createUserWithoutPasswordErrorTest() {

        user = new User(randomUser().getEmail(), null, randomUser().getName());
        userClient = new UserClient();

        response = userClient.create(user);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));

    }
}