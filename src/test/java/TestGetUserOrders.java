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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;


public class TestGetUserOrders {

    private UserData userData;
    private BurgerServiceClient client;
    String userAccessToken;
    String firstIngredientId;
    String secondIngredientId;
    private List<String> ingredientIds;
    private Random random = new Random();

    @Before
    public void setUp() {

        client = new BurgerServiceClient();
        userData = new UserData("emaiiahhsghhs15@yandex.ru", "password", "Elina");
        userAccessToken = client.createUserPostRequest(userData).extract().path("accessToken");
        ingredientIds = client.getValidIngredientIds();
        firstIngredientId = ingredientIds.get(random.nextInt(ingredientIds.size()));
        secondIngredientId = ingredientIds.get(random.nextInt(ingredientIds.size()));
        client.createOrder(Optional.of(userAccessToken), firstIngredientId, secondIngredientId);

    }

    @Test
    @DisplayName("Successful getting user's order with authorization")
    @Description("Positive test for GET request to /api/ingredients endpoint by using authorization accessToken")
    public void GetUserOrdersWithAuthorizationSucessfullyTest() {

        ValidatableResponse response = client.getUserOrders(Optional.of(userAccessToken));

        checkStatusCode200(response);
        checkResponseBody200(response);
        checkCountOfOrders(response);
    }

    @Test
    @DisplayName("Negative creating an order with authorization and not added ingredients")
    @Description("Negative test for GET request to /api/ingredients endpoint by not using authorization accessToken")
    public void GetUserOrdersWithoutAuthorizationImpossibleTest() {

        ValidatableResponse response = client.getUserOrders(Optional.empty());

        checkStatusCode401(response);
        checkResponseBody401(response);
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
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Step ("Check positive response structure {get no more than 50 orders}")
    public void checkCountOfOrders(ValidatableResponse response) {
        List<?> orders = response.extract().path("orders");
        assertThat(orders.size(), lessThanOrEqualTo(50));
    }

    @Step("Check negative creating response code (200)")
    public void checkStatusCode401(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Step ("Check negative response message {\"message\": \"You should be authorizes\"}")
    public void checkResponseBody401(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("You should be authorised"));
    }
}