import io.restassured.response.ValidatableResponse;
import org.example.StellarBurgerClient;
import org.example.User;
import org.example.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    @Before
    public void registerUser_success() {

        // Регистрация нового пользователя с динамическим email
        user = new User(System.currentTimeMillis() + "@mail.ru", "DashaDasha", "Dasha");
        ValidatableResponse response = client.registerUser (user);
        token = response.extract().jsonPath().getString("accessToken");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success",is(true));
    }


    @Test
    // Метод для успешной авторизации пользователя
    public void loginUser_success() {

        // Создание объекта UserCredentials на основе зарегистрированного пользователя
        UserCredentials creds = UserCredentials.fromUser(user, token);
        ValidatableResponse response = client.loginUser(creds);

        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    // Метод для попытки авторизации без пароля
    public void loginUserWithoutPassword() {

        // Создание объекта UserCredentials без пароля
        UserCredentials creds = UserCredentials.withoutPassword(user.getEmail(),token);
        ValidatableResponse response = client.loginUser(creds);

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
