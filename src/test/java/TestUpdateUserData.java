import client.BurgerServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.UserData;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Optional;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class TestUpdateUserData {

    private UserData userData;
    private BurgerServiceClient client;
    String userAccessToken;

    @Before
    public void setUp() {
        client = new BurgerServiceClient();
        userData = new UserData("emailspitsulya15@yandex.ru", "password", "Elina");
        userAccessToken = client.createUserPostRequest(userData).extract().path("accessToken");
    }

    @Test
    @DisplayName("Successful updating of a user data (password) with authorization")
    @Description("Positive test for PATCH request to /api/auth/user endpoint")
    public void UpdatingUserPasswordWithAuthSucessfullyTest() {

        Credentials credentials = Credentials.updatePassword(userData);
        ValidatableResponse response = client.updateUserData(Optional.of(userAccessToken), credentials);

        checkStatusCode200(response);
        checkResponseBody200(response);
    }

    @Test
    @DisplayName("Successful updating of a user data (email) with authorization")
    @Description("Positive test for PATCH request to /api/auth/user endpoint")
    public void UpdatingUserEmailWithAuthSucessfullyTest() {

        Credentials credentials = Credentials.updateEmail(userData);
        ValidatableResponse response = client.updateUserData(Optional.of(userAccessToken), credentials);

        checkStatusCode200(response);
        checkResponseBody200(response);
    }

    @Test
    @DisplayName("Successful updating of a user data (name) with authorization")
    @Description("Positive test for PATCH request to /api/auth/user endpoint")
    public void UpdatingUserNameWithAuthSucessfullyTest() {

        Credentials credentials = Credentials.updateName(userData);
        ValidatableResponse response = client.updateUserData(Optional.of(userAccessToken), credentials);

        checkStatusCode200(response);
        checkResponseBody200(response);
    }

    @Test
    @DisplayName("Negative updating of a user data (password) without authorization")
    @Description("Positive test for PATCH request to /api/auth/user endpoint")
    public void UpdatingUserPasswordWithoutAuthSucessfullyTest() {

        Credentials credentials = Credentials.updatePassword(userData);
        ValidatableResponse response = client.updateUserData(Optional.empty(), credentials);

        checkStatusCode401(response);
        checkResponseBody401(response);
    }

    @Test
    @DisplayName("Negative updating of a user data (email) without authorization")
    @Description("Positive test for PATCH request to /api/auth/user endpoint")
    public void UpdatingUserEmailWithoutAuthSucessfullyTest() {

        Credentials credentials = Credentials.updateEmail(userData);
        ValidatableResponse response = client.updateUserData(Optional.empty(), credentials);

        checkStatusCode401(response);
        checkResponseBody401(response);
    }

    @Test
    @DisplayName("Negative updating of a user data (name) without authorization")
    @Description("Positive test for PATCH request to /api/auth/user endpoint")
    public void UpdatingUserNameWithoutAuthSucessfullyTest() {

        Credentials credentials = Credentials.updateName(userData);
        ValidatableResponse response = client.updateUserData(Optional.empty(), credentials);

        checkStatusCode401(response);
        checkResponseBody401(response);
    }

    @After
    public void tearDown() {
        if (userAccessToken != null) {
            client.deleteUser(userAccessToken);
        }
    }

    @Step("Check positive update response code (200)")
    public void checkStatusCode200(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_OK);
    }

    @Step ("Check positive response message {success: true}")
    public void checkResponseBody200(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("success", equalTo(true));
    }

    @Step("Check positive update response code (200)")
    public void checkStatusCode401(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Step ("Check negative authorization response message {\"message\": \"You should be authorised\"}")
    public void checkResponseBody401(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", CoreMatchers.equalTo("You should be authorised"));
    }
}
