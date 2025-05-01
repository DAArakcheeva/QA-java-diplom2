package org.example;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.example.Ingredients.INVALID_INGREDIENT_ID;


public class StellarBurgerClient {

    // Статическая переменная для хранения базового URL
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";


    // Метод для регистрации нового пользователя
    @Step("Регистрация нового пользователя")
    public ValidatableResponse registerUser(User user) {
        return given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(user)
                // Отправка POST-запроса на конечную точку регистрации
                .post("/api/auth/register")
                .then()
                .log()
                .all();
    }

    // Метод для входа пользователя в систему
    @Step("Вход пользователя в систему")
    public ValidatableResponse loginUser(UserCredentials credentials) {
        return given()
                .log()
                .all()
                .auth()
                .oauth2(credentials.getToken())
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(credentials)
                // Отправка POST-запроса на конечную точку авторизации
                .post("/api/auth/login")
                .then()
                .log()
                .all();
    }


    // Метод для удаления пользователя
    @Step("Удаление пользователя")
    public void deleteUser(String token) {
        given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", token) // Добавляем токен в заголовок
                // Отправка DELETE-запроса на конечную точку пользователя
                .delete("/api/auth/user")
                .then()
                .log()
                .all();
    }

    // Метод для обновления данных пользователя
    @Step("Обновление данных пользователя")
    public ValidatableResponse updateUser(User updatedUser, String token) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(updatedUser)
                // Отправка PATCH-запроса на конечную точку пользователя
                .patch("/api/auth/user")
                .then()
                .log().all();
    }

    // Метод для обновления данных пользователя без авторизации
    @Step("Обновление данных пользователя без авторизации")
    public ValidatableResponse updateUserWithoutAuth(User updatedUser) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(updatedUser)
                // Отправка PATCH-запроса на конечную точку пользователя
                .patch("/api/auth/user")
                .then()
                .log().all();
    }

    // Метод для создания заказа
    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post("/api/orders")
                .then()
                .log().all();
    }

    // Метод для создания заказа без ингридиентов
    @Step("Создание заказа без ингридиентов")
    public ValidatableResponse createOrderWithNoIngredients(String ingredient) {
        if (ingredient.equals(" ")) {
            return given()
                    .log().all()
                    .baseUri(BASE_URL)
                    .header("Content-type", "application/json")
                    .body("{ }")
                    // Отправка POST-запроса на конечную точку создания заказа
                    .post("/api/orders")
                    .then().log().all();
        } else {
            return given()
                    .log().all()
                    .baseUri(BASE_URL)
                    .header("Content-type", "application/json")
                    .body(INVALID_INGREDIENT_ID)
                    // Отправка POST-запроса на конечную точку создания заказа
                    .post("/api/orders")
                    .then().log().all();
        }
    }

    // Метод для получения заказов пользователя
    @Step("Получаем заказ пользователя")
    public ValidatableResponse getUserOrders(String token){
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .header("Authorization", token)
                // Отправка GET-запроса на конечную точку создания заказа
                .get("/api/orders")
                .then().log().all();
    }

}