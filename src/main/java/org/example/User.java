package org.example;

public class User {

    private String email;
    private String password;
    private String name;

    // Конструктор класса User для инициализации полей
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Геттер для получения адреса электронной почты пользователя
    public String getEmail() {
        return email;
    }

    // Геттер для получения пароля пользователя
    public String getPassword() {
        return password;
    }

    // Геттер для получения имени пользователя
    public String getName() {
        return name;
    }

}