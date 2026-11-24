package ru.edu.pr07;

import java.util.Objects;

public record DeliveryRequest(DeliveryType type, double distanceKm, double weightKg, double orderAmount) {
    public DeliveryRequest {
        Objects.requireNonNull(type, "Способ доставки не может быть null");
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Расстояние не может быть отрицательным: " + distanceKm);
        }
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Вес посылки должен быть больше нуля: " + weightKg);
        }
        if (orderAmount < 0) {
            throw new IllegalArgumentException("Стоимость заказа не может быть отрицательной: " + orderAmount);
        }
    }
}
