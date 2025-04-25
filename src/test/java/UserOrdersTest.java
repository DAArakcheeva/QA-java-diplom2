import io.qameta.allure.Step;
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

    @Step("Регистрация и авторизация пользователя")
    private void registerUserAndLogin() {

        // Регистрация нового пользователя с динамическим email
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

    @Before
    @Step("Подготовка среды для тестов")
    public void setUp() {

        registerUserAndLogin();
    }

    @Test
    @Step("Получение заказов с авторизацией")
    public void getUserOrdersWithAuthSuccess() {

        ValidatableResponse response = client.getUserOrders(token);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Step("Получение заказов без авторизации")
    public void getUserOrdersWithoutAuthFail() {

        ValidatableResponse response = client.getUserOrders("");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(401)
                .body("success", is(false));
    }

    @After
    @Step("Удаление пользователя после завершения теста")
    public void deleteUserAfterTest() {
        // Проверка наличия токена
        if (token != null) {
            // Удаление пользователя через клиентский API
            client.deleteUser(token);

        }
    }
}
