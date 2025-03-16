import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import client.BurgerServiceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import com.github.javafaker.Faker;

public class TestCreateUser {

    private UserData userData;
    private BurgerServiceClient client;
    String userAccessToken;
    private Faker faker;

    @Before
    public void setUp() {
        client = new BurgerServiceClient();
        faker = new Faker();
    }

    @Test
    @DisplayName("Successful user creation with all fields")
    @Description("Positive test for POST request to /api/auth/register endpoint by filling in all required fields")
    public void createUserSuccessfullyTest() {

        // Пользователь со всеми валидными данными
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(6, 10), faker.name().firstName());
        ValidatableResponse response = client.createUserPostRequest(userData);

        checkStatusCode200(response);
        checkResponseBody200(response);
    }


    @Test
    @DisplayName("Unsuccessful creation of two identical users")
    @Description("Negative test for POST request to /api/auth/register endpoint by using the same user's data")
    public void createTwoIdenticalUsersImpossibleTest() {

        // Пользователь со всеми валидными данными
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(6, 10), faker.name().firstName());

        ValidatableResponse response = client.createUserPostRequest(userData);;
        checkStatusCode200(response);

        ValidatableResponse response1 = client.createUserPostRequest(userData);;
        checkStatusCode403(response1);
        checkResponseBody403UserExists(response1);
    }

    @Test
    @DisplayName("Unsuccessful creation without user's email")
    @Description("Negative test for POST request to /api/auth/register endpoint by not using all required fields")
    public void createUserWithoutEmailImpossibleTest() {

        // Пользователь с невалидным пустым email
        userData = new UserData("", faker.internet().password(6, 10), faker.name().firstName());
        ValidatableResponse response = client.createUserPostRequest(userData);

        checkStatusCode403(response);
        checkResponseBody403RequiredFields(response);
    }

    @Test
    @DisplayName("Unsuccessful creation without user's password")
    @Description("Negative test for POST request to /api/auth/register endpoint by not using all required fields")
    public void createUserWithoutPasswordImpossibleTest() {

        // Пользователь с невалидным пустым паролем
        userData = new UserData(faker.internet().emailAddress(), "", faker.name().firstName());
        ValidatableResponse response = client.createUserPostRequest(userData);

        checkStatusCode403(response);
        checkResponseBody403RequiredFields(response);
    }

    @Test
    @DisplayName("Unsuccessful user creation without user's name")
    @Description("Negative test for POST request to /api/auth/register endpoint by not using all required fields")
    public void createUserWithoutNameImpossibleTest() {

        // Пользователь с невалидным пустым именем
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(6, 10), "");
        ValidatableResponse response = client.createUserPostRequest(userData);

        checkStatusCode403(response);
        checkResponseBody403RequiredFields(response);
    }


    @After
    public void tearDown() {
        userAccessToken = client.createUserPostRequest(userData).extract().path("accessToken");

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
    public void checkStatusCode403(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @Step ("Check negative response message {\"message\": \"User already exists\"}")
    public void checkResponseBody403UserExists(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("User already exists"));
    }


    @Step ("Check negative response message {\"message\": \"Email, password and name are required fields\"}")
    public void checkResponseBody403RequiredFields(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
