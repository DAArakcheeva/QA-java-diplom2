import io.qameta.allure.Step;
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
    @Step("Регистрация нового пользователя")
    public void registerUserSuccess() {

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

    @Test
    @Step("Обновление почты и имени пользователя")
    public void updateEmailAndNameFail() {

        String newEmail = "NEW_" + user.getEmail();
        String newName = "NEW_" + user.getName();
        User updatedUser = new User();
        updatedUser.setEmail(newEmail);
        updatedUser.setPassword(user.getPassword());
        updatedUser.setName(newName);
        // Отправка запроса на обновление данных пользователя без авторизации
        ValidatableResponse response = client.updateUserWithoutAuth(updatedUser);
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
