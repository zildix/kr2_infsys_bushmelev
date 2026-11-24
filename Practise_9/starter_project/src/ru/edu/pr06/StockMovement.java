package ru.edu.pr06;

import java.util.Objects;

public record StockMovement(String itemCode, MovementType type, int quantity) {
    public StockMovement {
        if (itemCode == null || itemCode.isBlank()) {
            throw new IllegalArgumentException("Код позиции не может быть пустым");
        }
        Objects.requireNonNull(type, "Тип операции не может быть null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество в операции должно быть положительным");
        }
    }
}
