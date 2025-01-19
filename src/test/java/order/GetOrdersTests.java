package order;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.OrderClient;
import stellarburgers.client.UserClient;
import stellarburgers.models.User;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static stellarburgers.generators.UserGenerator.randomUser;

public class GetOrdersTests {

    private User user;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    ValidatableResponse response;
    ValidatableResponse loginResponse;

    @Before
    public void setUp() {
        user = new User();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Получение заказов конкретного пользователя после авторизации")
    public void getOrdersAfterAuthorizationTest() {
        user = randomUser();
        userClient.create(user);
        loginResponse = userClient.login(user);
        accessToken = loginResponse.extract().path("accessToken");

        response = orderClient.getOrdersAfterAuthorization(accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("orders", instanceOf(List.class));
    }

    @Test
    @Description("Получение заказов конкретного пользователя без авторизации")
    public void getOrdersWithoutAuthorizationErrorTest() {
        user = randomUser();
        userClient.create(user);

        response = orderClient.getOrdersWithoutAuthorization();

        response.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}
