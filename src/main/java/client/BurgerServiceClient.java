package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import model.constants.Endpoints;
import model.constants.Url;
import model.Credentials;

import static io.restassured.RestAssured.given;

public class BurgerServiceClient {

    private String baseURI;

    public BurgerServiceClient() {
        this.baseURI = Url.BASE_URI;
    }

    @Step("User creating with data")
    public ValidatableResponse createUserPostRequest(UserData userData) {

        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .body(userData)
                .when()
                .post(Endpoints.CREATE_USER)
                .then()
                .log()
                .all();
    }

    @Step("User authorization with existing data")
    public ValidatableResponse authorizeUser(Credentials credentials) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .body(credentials)
                .post(Endpoints.LOGIN_USER)
                .then()
                .log()
                .all();
    }

    @Step("User removing with accessToken and completing the test, DELETE /api/auth/user")
    public ValidatableResponse deleteUser(String userAccessToken) {

        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .header("Authorization", userAccessToken)
                .when()
                .delete(Endpoints.DELETE_USER)
                .then()
                .log()
                .all()
                .statusCode(202);
    }
}
