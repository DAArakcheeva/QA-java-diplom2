import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.StellarBurgerClient;
import org.example.User;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class RegisterUserTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    @Test
    @DisplayName("Регистрация пользователя")
    public void registerUser_success() {

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
    @DisplayName("Проверка на невозможность регистрирование дубликата")
    public void registerUserDuplicate() {

        // Вызов успешного метода регистрации пользователя
        registerUser_success();
        // Повторное создание пользователя с теми же данными
        user = new User(user.getEmail(), user.getPassword(), user.getName());
        ValidatableResponse response = client.registerUser(user);
        token = response.extract().jsonPath().getString("accessToken");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(403)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Невозможность зарегистрировать пользователя без поля пароля")
    public void registerUserWithoutPass() {

        // Регистрация нового пользователя без пароля
        user = new User(System.currentTimeMillis() + "@mail.ru", null, "Dasha");
        ValidatableResponse response = client.registerUser(user);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(403)
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
