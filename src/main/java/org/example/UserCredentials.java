package org.example;

public class UserCredentials {

    private final String email;
    private final String password;
    private final String token;

    // Приватный конструктор класса UserCredentials для инициализации полей
    private UserCredentials(String email, String password, String token) {
        this.email = email;
        this.password = password;
        this.token = token;
    }

    // Статический метод для создания объекта UserCredentials на основе объекта User и токена
    public static UserCredentials fromUser(User user, String token) {
        return new UserCredentials(user.getEmail(), user.getPassword(), token);
    }

    // Статический метод для создания объекта UserCredentials без пароля
    public static UserCredentials withoutPassword(String email, String token) {
        return new UserCredentials(email, null, token);
    }

    // Геттер для получения адреса электронной почты пользователя
    public String getEmail() {
        return email;
    }

    // Геттер для получения пароля пользователя
    public String getPassword() {
        return password;
    }

    // Геттер для получения токена пользователя
    public String getToken() {
        return token;
    }
}
