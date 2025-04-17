package org.example;

import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class StellarBurgerClient {

    // Статическая переменная для хранения базового URL
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

    // Идентификатор "Флюоресцентная булка"
    public static final String FLU_BUN_ID = "61c0c5a71d1f82001bdaaa6d";

    // Строка с неправильным идентификатором ингредиента для тестирования
    public static final String INVALID_INGREDIENT_ID = "{\n \"ingredients\": [\"1234543254\"]\n" + "}";

    // Метод для регистрации нового пользователя
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
    public ValidatableResponse createOrder(String ingredient) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body("{\n \"ingredients\": [\"" + ingredient + "\"] \n}")
                // Отправка POST-запроса на конечную точку создания заказа
                .post("/api/orders")
                .then().log().all();
    }

    // Метод для создания заказа без ингридиентов
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