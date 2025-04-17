import io.restassured.response.ValidatableResponse;
import org.example.StellarBurgerClient;
import org.example.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class UpdUserWithoutAuthTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    @Before
    public void registerUser_success() {

        // Регистрация нового пользователя с динамическим email
        user = new User(System.currentTimeMillis() + "@mail.ru", "DashaDasha", "Dasha");
        ValidatableResponse response = client.registerUser(user);
        token = response.extract().jsonPath().getString("accessToken");
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    // Обновление почты и имени пользователя
    public void updateEmailAndName_fail() {

        String newEmail = "NEW_" + user.getEmail();
        String newName = "NEW_" + user.getName();
        User updatedUser = new User(newEmail, user.getPassword(), newName);
        // Отправка запроса на обновление данных пользователя без авторизации
        ValidatableResponse response = client.updateUserWithoutAuth(updatedUser);
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
