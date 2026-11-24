package ru.edu.pr09;

import java.util.Objects;

public record Sale(String id, String manager, String region, double amount) {
    public Sale {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Идентификатор сделки не может быть пустым");
        }
        if (manager == null || manager.isBlank()) {
            throw new IllegalArgumentException("Имя менеджера не может быть пустым");
        }
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Регион не может быть пустым");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Сумма сделки должна быть неотрицательным числом: " + amount);
        }
    }
}
