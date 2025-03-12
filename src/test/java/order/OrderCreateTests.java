package order;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.OrderClient;
import stellarburgers.client.UserClient;
import stellarburgers.models.User;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static stellarburgers.client.OrderClient.LOGIN_URI;
import static stellarburgers.generators.UserGenerator.randomUser;

public class OrderCreateTests {

    private User user;
    private UserClient userClient;
    private OrderClient orderClient;
    private List<String> ingredients;
    private String accessToken;

    String ingredientId1;
    String ingredientId2;
    String ingredientId3;
    String incorrectIngredient = randomAlphabetic(25);

    ValidatableResponse loginResponce;
    ValidatableResponse response;

    @Before
    public void setUp() {
        user = randomUser();
        userClient = new UserClient();
        userClient.create(user);

        orderClient = new OrderClient();
        ingredients = new ArrayList<>();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание заказа после авторизации")
    public void orderCreateAfterAuthorizationTest() {
        loginResponce = userClient.login(user);
        accessToken = loginResponce.extract().path("accessToken");

        ingredientId1 = orderClient.getIngredientsIds().get(1);
        ingredientId2 = orderClient.getIngredientsIds().get(2);
        ingredientId3 = orderClient.getIngredientsIds().get(3);

        ingredients.add(ingredientId1);
        ingredients.add(ingredientId2);
        ingredients.add(ingredientId3);

        response = orderClient.orderCreateAfterAuthorization(ingredients, accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.number", isA(Integer.class));
    }

    @Test
    @Description("Создание заказа без авторизации")
    public void orderCreateWithoutAuthorizationErrorTest() {
        ingredientId1 = orderClient.getIngredientsIds().get(1);
        ingredientId2 = orderClient.getIngredientsIds().get(2);
        ingredientId3 = orderClient.getIngredientsIds().get(3);

        ingredients.add(ingredientId1);
        ingredients.add(ingredientId2);
        ingredients.add(ingredientId3);

        response = orderClient.orderCreateWithoutAuthorization(ingredients);

        response.log().all()
                .assertThat()
                .statusCode(oneOf(SC_MOVED_PERMANENTLY, SC_MOVED_PERMANENTLY,
                        SC_SEE_OTHER, SC_TEMPORARY_REDIRECT))
                .header("Location", equalTo(LOGIN_URI));
    }

    @Test
    @Description("Создание заказа после авторизации без ингредиентов")
    public void orderCreateAfterAuthorizationWithoutIngredientsErrorTest() {
        loginResponce = userClient.login(user);
        accessToken = loginResponce.extract().path("accessToken");

        ingredientId1 = orderClient.getIngredientsIds().get(1);
        ingredients.add(ingredientId1);
        ingredients.clear();

        response = orderClient.orderCreateAfterAuthorization(ingredients, accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа после авторизации с неверным хешем ингредиентов")
    public void orderCreateAfterAuthorizationIncorrectIngredientHashErrorTest() {
        loginResponce = userClient.login(user);
        accessToken = loginResponce.extract().path("accessToken");

        ingredients.add(incorrectIngredient);

        response = orderClient.orderCreateAfterAuthorization(ingredients, accessToken);

        response.log().all()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
