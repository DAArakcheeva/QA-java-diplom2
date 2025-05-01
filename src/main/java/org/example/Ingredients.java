package org.example;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;


public class Ingredients {

    // Статическая переменная для хранения базового URL
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";
    public static final String INGREDIENTS = "/api/ingredients";

    // Строка с неправильным идентификатором ингредиента для тестирования
    public static final String INVALID_INGREDIENT_ID = "{\n \"ingredients\": [\"1234543254\"]\n" + "}";

    @Step("Выполнить запрос на получение списка ингредиентов")
    public ValidatableResponse fetchIngredientList() {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .when()
                .get(INGREDIENTS)
                .then();
    }
}
