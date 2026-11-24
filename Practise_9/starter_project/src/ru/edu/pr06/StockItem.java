package ru.edu.pr06;

import java.util.Objects;

public class StockItem {
    // Параметр варианта 1: минимальный неснижаемый остаток на складе
    public static final int MIN_RESERVE = 10;

    private final String code;
    private final String name;
    private final Category category;
    private int quantity;

    public StockItem(String code, String name, Category category, int quantity) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Инвентарный код не должен быть пустым");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Наименование не должно быть пустым");
        }
        Objects.requireNonNull(category, "Категория не может быть null");
        if (quantity < 0) {
            throw new IllegalArgumentException("Начальный остаток не может быть отрицательным");
        }
        this.code = code.trim();
        this.name = name.trim();
        this.category = category;
        this.quantity = quantity;
    }

    public void increase(int q) {
        validatePositiveAmount(q);
        this.quantity += q;
    }

    public void decrease(int q) {
        validatePositiveAmount(q);
        if (this.quantity - q < MIN_RESERVE) {
            throw new IllegalStateException(
                "Невозможно списать " + q + " ед. (код " + code + "): остаток не может стать меньше резерва (" + MIN_RESERVE + ")"
            );
        }
        this.quantity -= q;
    }

    private void validatePositiveAmount(int q) {
        if (q <= 0) {
            throw new IllegalArgumentException("Количество должно быть строго больше нуля: " + q);
        }
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockItem stockItem)) return false;
        return Objects.equals(code, stockItem.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code + " (" + name + ", " + category + "): " + quantity;
    }
}
