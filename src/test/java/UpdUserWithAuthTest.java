import io.restassured.response.ValidatableResponse;
import org.example.StellarBurgerClient;
import org.example.User;
import org.example.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class UpdUserWithAuthTest {

    private StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private String token;

    @Before
    public void registerUser_success() {

        // Регистрация нового пользователя с динамическим email
        user = new User(System.currentTimeMillis() + "@mail.ru", "DashaDasha", "Dasha");
        ValidatableResponse response = client.registerUser(user);
        token = response.extract().jsonPath().getString("accessToken");

        // Выводим ответ сервера для проверки
        System.out.println(response.toString());

        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));

        if (token == null) {
            throw new RuntimeException("Token was not extracted from the server response!");
        }
    }

    // Метод для успешной авторизации пользователя
    public void loginUser_success() {

        // Проверки текущих значений user и token
        System.out.println("User: " + user);
        System.out.println("Token: " + token);

        if (user == null || token == null) {
            throw new IllegalStateException("User or token cannot be null.");
        }

        // Создание объекта UserCredentials на основе зарегистрированного пользователя
        UserCredentials creds = UserCredentials.fromUser(user, token);
        ValidatableResponse response = client.loginUser(creds);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    // Обновление почты и имени пользователя
    public void updateEmailAndName_success() {

        // Генерация нового email и имя пользователя на основе существующего
        String newEmail = "upd_" + user.getEmail();
        String newName = "UPD_" + user.getName();

        User updatedUser = new User(newEmail, user.getPassword(), newName);
        // Отправляем запрос на обновление данных пользователя
        ValidatableResponse response = client.updateUser(updatedUser, token);
        // Проверка ответа сервера
        response.assertThat()
                .statusCode(200)
                .body("success", is(true));

        // Проверяем, что email и имя пользователя обновлены
        response.assertThat().body("user.email", is(newEmail)); // Проверка, что email обновлен
        response.assertThat().body("user.name", is(newName));   // Проверка, что имя обновлено

    }

    @After
    // Метод для удаления пользователья после завершения теста
    public void deleteUser_afterTest() {
        // Проверка наличия токена
        if (token != null) {
            client.deleteUser(token);
        }
    }
}
