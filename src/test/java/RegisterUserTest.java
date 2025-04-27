import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.StellarBurgerClient;
import org.example.User;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

public class RegisterUserTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    @Test
    @DisplayName("Регистрация пользователя")
    @Step("Регистрация нового пользователя")
    public void registerUserSuccess() {

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
    @DisplayName("Проверка на невозможность регистрирование дубликата")
    @Step("Регистрация пользователя-дубликата")
    public void registerUserDuplicate() {

        // Первоначальная успешная регистрация пользователя
        registerUserSuccess();

        // Повторная попытка регистрации с теми же данными
        user = new User();
        user.setEmail("test@example.com"); // фиксированный email
        user.setPassword("DashaDasha");
        user.setName("Dasha");
        ValidatableResponse response = client.registerUser(user);

        // Проверка ответа сервера
        response.assertThat()
                .statusCode(403)
                .body("success", is(false))
                .body("message", containsString("User already exists"));
    }

    @Test
    @DisplayName("Невозможность зарегистрировать пользователя без поля пароля")
    @Step("Регистрация пользователя без пароля")
    public void registerUserWithoutPass() {
        // Регистрация нового пользователя без пароля
        user = new User();
        user.setEmail(System.currentTimeMillis() + "@mail.ru");
        user.setPassword(null);
        user.setName("Dasha");
        ValidatableResponse response = client.registerUser(user);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(403)
                .body("success", is(false));
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
