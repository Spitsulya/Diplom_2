import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.UserData;
import client.BurgerServiceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class TestCreateUser {

    private UserData userData;
    private BurgerServiceClient client;

    @Before
    public void setUp() {
        client = new BurgerServiceClient();
    }

    @Test
    @DisplayName("Successful user creation")
    @Description("Positive test for POST request to /api/auth/register endpoint by filling in all required fields")
    public void CreateUserSucessfullyTest() {

        userData = new UserData("maalishka15@yandex.ru", "password", "elina");
        ValidatableResponse response = client.createUserPostRequest(userData);

        checkStatusCode200(response);
        checkResponseBody200(response);
    }

    @Test
    @DisplayName("Unsuccessful creation of two identical users")
    @Description("Negative test for POST request to /api/auth/register endpoint by using the same user's data")
    public void CreateTwoIdenticalUsersImpossibleTest() {

        userData = new UserData("mmalishka15@yandex.ru", "password", "elina");

        ValidatableResponse response = client.createUserPostRequest(userData);;
        checkStatusCode200(response);

        ValidatableResponse response1 = client.createUserPostRequest(userData);;
        checkStatusCode403e(response1);
        checkResponseBody403e(response1);
    }

    @Test
    @DisplayName("Unsuccessful creation without user's email")
    @Description("Negative test for POST request to /api/auth/register endpoint by not using all required fields")
    public void CreateUserWithoutEmailImpossibleTest() {

        userData = new UserData("", "1234", "elina");
        ValidatableResponse response = client.createUserPostRequest(userData);

        checkStatusCode403f(response);
        checkResponseBody403f(response);
    }

    @Test
    @DisplayName("Unsuccessful creation without user's password")
    @Description("Negative test for POST request to /api/auth/register endpoint by not using all required fields")
    public void CreateUserWithoutPasswordImpossibleTest() {

        userData = new UserData("malishka15@yandex.ru", "", "elina");
        ValidatableResponse response = client.createUserPostRequest(userData);

        checkStatusCode403f(response);
        checkResponseBody403f(response);
    }

    @After
    public void tearDown() {
        String userAccessToken = null;

        try {
            userAccessToken = client.authorizeUser(Credentials.fromUserData(userData))
                    .extract().path("accessToken"); // Получаем accessToken, если запрос успешен
        } catch (Exception e) {
            System.out.println("Authorization failed or user does not exist. Skipping deletion.");
        }

        // Удаляем пользователя, если accessToken получен
        if (userAccessToken != null) {
            client.deleteUser(userAccessToken);
        }
    }


    @Step("Check positive response code (200 OK)")
    public void checkStatusCode200(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_OK);
    }

    @Step ("Check positive response message {success: true}")
    public void checkResponseBody200(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("success", equalTo(true));
    }

    @Step ("Check negative response code (403 Forbidden)")
    public void checkStatusCode403e(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @Step ("Check negative response message {\"message\": \"User already exists\"}")
    public void checkResponseBody403e(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("User already exists"));
    }

    @Step ("Check negative response code (403 Forbidden)")
    public void checkStatusCode403f(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @Step ("Check negative response message {\"message\": \"Email, password and name are required fields\"}")
    public void checkResponseBody403f(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
