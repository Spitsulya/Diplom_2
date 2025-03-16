import client.BurgerServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import com.github.javafaker.Faker;

public class TestCreatingOrder {

    private UserData userData;
    private BurgerServiceClient client;
    String userAccessToken;
    private List<String> ingredientIds;
    private Random random = new Random();
    private Faker faker;

    @Before
    public void setUp() {

        client = new BurgerServiceClient();
        faker = new Faker();

        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(6, 10), faker.name().firstName());
        userAccessToken = client.createUserPostRequest(userData).extract().path("accessToken");
        ingredientIds = client.getValidIngredientIds();
    }

    @Test
    @DisplayName("Successful creating an order with authorization and added ingredients")
    @Description("Positive test for POST request to /api/orders endpoint by filling in valid ingredients and authorization accessToken")
    public void creatingOrderWithIngredientsAndAuthSuccessfullyTest() {

        String firstIngredientId = ingredientIds.get(random.nextInt(ingredientIds.size()));
        String secondIngredientId = ingredientIds.get(random.nextInt(ingredientIds.size()));

        ValidatableResponse response = client.createOrder(Optional.of(userAccessToken), firstIngredientId, secondIngredientId);

        checkStatusCode200(response);
        checkResponseBody200(response);
    }

    @Test
    @DisplayName("Negative creating an order with authorization and without ingredients")
    @Description("Negative test for POST request to /api/orders endpoint by filling in authorization accessToken without added ingredients")
    public void creatingOrderWithoutIngredientsAndWithAuthImpossibleTest() {

        ValidatableResponse response = client.createOrder(Optional.of(userAccessToken), null, null);

        checkStatusCode400(response);
        checkResponseBody400(response);
    }

    @Test
    @DisplayName("Negative creating an order with authorization and added invalid ingredients")
    @Description("Negative test for POST request to /api/orders endpoint by filling in authorization accessToken and invalid ingredients")
    public void creatingOrderWithInvalidIngredientsAndWithAuthImpossibleTest() {

        String invalidIngredientId1 = "test1";
        String invalidIngredientId2 = "test2";

        ValidatableResponse response = client.createOrder(Optional.of(userAccessToken), invalidIngredientId1, invalidIngredientId2);

        checkStatusCode500(response);
        checkResponseBody500(response);
    }

    @Test
    @DisplayName("Negative order creation with ingredients and without authorization")
    @Description("Negative test for POST request to /api/orders endpoint by filling in valid ingredients and not filling in authorization accessToken")
    public void creatingOrderWithIngredientsAndWithoutAuthImpossibleTest() {

        String firstIngredientId = ingredientIds.get(random.nextInt(ingredientIds.size()));
        String secondIngredientId = ingredientIds.get(random.nextInt(ingredientIds.size()));

        ValidatableResponse response = client.createOrder(Optional.empty(), firstIngredientId, secondIngredientId);

        // Не авторизованные пользователи не могут создать заказ
        checkStatusCode400(response);
    }

    @After
    public void tearDown() {
        if (userAccessToken != null) {
            client.deleteUser(userAccessToken);
        }
    }

    @Step("Check positive creating response code (200)")
    public void checkStatusCode200(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_OK);
    }

    @Step ("Check positive response message {success: true}")
    public void checkResponseBody200(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("success", equalTo(true));
    }

    @Step("Check negative creating response code (400 Bad request)")
    public void checkStatusCode400(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Step ("Check negative response message {\"message\": \"Ingredient ids must be provided\"}")
    public void checkResponseBody400(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Check negative creating response code (500 Internal server error)")
    public void checkStatusCode500(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Step ("Check negative response message {success: true}")
    public void checkResponseBody500(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .body(containsString("Internal Server Error"));
    }
}
