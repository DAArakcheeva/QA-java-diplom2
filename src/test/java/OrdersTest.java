import io.restassured.response.ValidatableResponse;
import org.example.StellarBurgerClient;
import org.example.User;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class OrdersTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    private void registerUserAndLogin() {

        // Создание нового пользователя с динамическим email
        user = new User(System.currentTimeMillis() + "@mail.ru", "DashaDasha", "Dasha");
        ValidatableResponse response = client.registerUser(user);
        token = response.extract().jsonPath().getString("accessToken");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    // Создание заказа с авторизацией
    public void createOrderWithAuth_success() {

        registerUserAndLogin();
        ValidatableResponse response1 = client.createOrder(StellarBurgerClient.FLU_BUN_ID);
        // Проверка ответа сервера
        response1.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    // Создание заказа без авторизации
    public void createOrderWithoutAuth_success() {

        ValidatableResponse response = client.createOrder(StellarBurgerClient.FLU_BUN_ID);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true)); //баг с 200 статус кодом
    }


    @Test
    // Создание заказа с авторизацией и без ингридиентов
    public void createOrderWithAuthAndNoIngredients_fail(){

        registerUserAndLogin();
        ValidatableResponse response = client.createOrderWithNoIngredients(" ");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    // Создание заказа с авторизацией и с неверным хешем ингредиентов
    public void createOrderWithAuthAndInvalidIngredient_fail() {

        registerUserAndLogin();
        ValidatableResponse response = client.createOrderWithNoIngredients(StellarBurgerClient.INVALID_INGREDIENT_ID);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(500);
    }

    @After
    // Метод для удаления пользователья после завершения теста
    public void deleteUser_afterTest() {
        // Проверка наличия токена
        if (token != null) {
            // Удаление пользователя через клиентский API
            client.deleteUser(token);
        }
    }
}
