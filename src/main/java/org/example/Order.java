package org.example;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private List<String> ingredients;

    // Добавляем конструктор
    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
