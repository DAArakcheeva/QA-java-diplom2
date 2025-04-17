import io.restassured.response.ValidatableResponse;
import org.example.StellarBurgerClient;
import org.example.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class UserOrdersTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    private void registerUserAndLogin() {

        // Регистрация нового пользователя с динамическим email
        user = new User(System.currentTimeMillis() + "@mail.ru", "DashaDasha", "Dasha");
        ValidatableResponse response = client.registerUser(user);
        token = response.extract().jsonPath().getString("accessToken");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Before
    public void setUp() {

        registerUserAndLogin();
    }

    @Test
    // Получение заказов с авторизацией
    public void getUserOrdersWithAuth_success() {

        ValidatableResponse response = client.getUserOrders(token);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    // Получение заказов без авторизации
    public void getUserOrdersWithoutAuth_fail() {

        ValidatableResponse response = client.getUserOrders("");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(401)
                .body("success", is(false));
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
