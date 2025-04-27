import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.Order;
import org.example.StellarBurgerClient;
import org.example.Ingredients;
import org.example.User;
import org.junit.After;
import org.junit.Test;


import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrdersTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    private void registerUserAndLogin() {

        // Создание нового пользователя с динамическим email
        user = new User();
        user.setEmail(System.currentTimeMillis() + "@mail.ru");
        user.setPassword("DashaDasha");
        user.setName("Dasha");
        ValidatableResponse response = client.registerUser(user);
        token = response.extract().jsonPath().getString("accessToken");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Step("Создание заказа с авторизацией")
    @DisplayName("Создать заказ успешно с авторизованным пользователем")
    public void createOrderWithAuthSuccess() {
        // Регистрация и авторизация пользователя
        registerUserAndLogin();

        // Получаем список ингредиентов
        ValidatableResponse responseIngredients = new Ingredients().fetchIngredientList();
        List<String> validIngredients = extractIngredientsFromResponse(responseIngredients);

        // Формируем объект заказа
        Order order = new Order(validIngredients.subList(0, 1)); // выбираем первые подходящие ингредиенты

        // Создание заказа с авторизацией
        ValidatableResponse response = client.createOrder(order, token);

        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue()); // проверка существования номера заказа
    }

    // Метод для извлечения списка ингредиентов из ответа
    private List<String> extractIngredientsFromResponse(ValidatableResponse response) {
        return response.extract().path("data._id");
    }

    @Test
    @Step("Создание заказа без авторизации")
    @DisplayName("Ошибка при создании заказа без авторизации")
    @Issue("Тест не проходит из-за бага в системе")
    public void createOrderWithoutAuthUnauthorized() {
        // Получаем список ингредиентов
        ValidatableResponse responseIngredients = new Ingredients().fetchIngredientList();
        List<String> validIngredients = extractIngredientsFromResponse(responseIngredients);

        // Формируем объект заказа
        Order order = new Order(validIngredients.subList(0, 1));

        // Попытка создать заказ без авторизации
        ValidatableResponse response = client.createOrder(order, ""); // пустой токен символизирует отсутствие авторизации

        // Проверка ответа сервера
        response.assertThat()
                .statusCode(401) // ожидаем статус Unauthorized
                .body("success", is(false));
    }

    @Test
    @Step("Создание заказа с авторизацией и без ингредиентов")
    @DisplayName("Ошибка при создании заказа с пустым списком ингредиентов")
    public void createOrderWithAuthAndNoIngredientsFail(){

        registerUserAndLogin();
        ValidatableResponse response = client.createOrderWithNoIngredients(" ");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @Step("Создание заказа с авторизацией и с неверным хешем ингредиентов")
    @DisplayName("Ошибка при создании заказа с некорректными ингредиентами")
    public void createOrderWithAuthAndInvalidIngredientFail() {

        registerUserAndLogin();
        ValidatableResponse response = client.createOrderWithNoIngredients(Ingredients.INVALID_INGREDIENT_ID);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(500);
    }

    @After
    @Step("Удаление пользователя после завершения теста")
    @DisplayName("Удалить пользователя после прохождения всех тестов")
    public void deleteUserAfterTest() {
        // Проверка наличия токена
        if (token != null) {
            // Удаление пользователя через клиентский API
            client.deleteUser(token);
        }
    }
}
