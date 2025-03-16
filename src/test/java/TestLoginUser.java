import client.BurgerServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import com.github.javafaker.Faker;

public class TestLoginUser {

    private UserData userData;
    private BurgerServiceClient client;
    String userAccessToken;
    private Faker faker;

    @Before
    public void setUp() {
        client = new BurgerServiceClient();
        faker = new Faker();
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(6, 10), faker.name().firstName());
        userAccessToken = client.createUserPostRequest(userData).extract().path("accessToken");
    }

    @Test
    @DisplayName("Successful authorization of a user")
    @Description("Positive test for POST request to /api/auth/login endpoint by filling in existing data and checking tokens")
    public void authorizationUserSucessfullyTest() {

        Credentials credentials = Credentials.fromUserData(userData);
        ValidatableResponse response = client.authorizeUser(credentials);

        checkStatusCode200(response);
        checkForTokensAvailability(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization with non-existent user's data")
    @Description("Negative test for POST request to /api/auth/login endpoint by using invalid user's login")
    public void authorizeUserWithInvalidLoginTest() {

        Credentials credentials = Credentials.invalidLogin(userData);
        ValidatableResponse response = client.authorizeUser(credentials);

        checkStatusCode401(response);
        checkResponseBody401(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization with non-existent courier's data")
    @Description("Negative test for POST request to /api/auth/login endpoint by using invalid user's password")
    public void authorizeUserWithInvalidPasswordTest() {

        Credentials credentials = Credentials.invalidPassword(userData);
        ValidatableResponse response = client.authorizeUser(credentials);

        checkStatusCode401(response);
        checkResponseBody401(response);
    }

    @After
    public void tearDown() {
        if (userAccessToken != null) {
            client.deleteUser(userAccessToken);
        }
    }

    @Step("Check positive login response code (200)")
    public void checkStatusCode200(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_OK);
    }

    @Step ("Check for user's accessToken and refreshToken in response")
    public void checkForTokensAvailability(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue());
    }

    @Step ("Check negative authorization response code (401 Unauthorized)")
    public void checkStatusCode401(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Step ("Check negative authorization response message {\"message\": \"email or password are incorrect\"}")
    public void checkResponseBody401(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("email or password are incorrect"));
    }

}
