package client;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import model.UserData;
import model.constants.Endpoints;
import model.constants.Url;
import model.Credentials;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

    @Step("User updating data with accessToken, PATCH /api/auth/user")
    public ValidatableResponse updateUserData(Optional<String> userAccessToken, Credentials credentials) {

        RequestSpecification spec = given();
        if (userAccessToken.isPresent()) {
            String token = userAccessToken.get();
            spec.header("Authorization", token);
        }
        return spec
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .contentType("application/json")
                .body(credentials)
                .patch(Endpoints.UPDATE_USER)
                .then()
                .log()
                .all();
    }

    @Step("Getting list of Burger's valid ingredients for creating a burger, GET /api/ingredients")
    public Response getValidIngredientIds() {
        // Получаем список ингредиентов
          return given()
                .baseUri(baseURI)
                .when()
                .get(Endpoints.GET_ORDER)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("User updating data with accessToken, PATCH /api/auth/user")
    public ValidatableResponse createOrder(Optional<String> userAccessToken, String firstIngredientId, String secondIngredientId) {

        RequestSpecification spec = given();
        if (userAccessToken.isPresent()) {
            String token = userAccessToken.get();
            spec.header("Authorization", token);
        }
        return spec
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .contentType("application/json")
                .body(new HashMap<String, Object>() {{
                    put("ingredients", Arrays.asList(firstIngredientId, secondIngredientId));
                }})
                .post(Endpoints.CREATE_ORDER)
                .then()
                .log()
                .all();
    }


}
