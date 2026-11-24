package ru.edu.pr07;

import java.util.Objects;

public class DeliveryService {
    private final DeliveryPolicyFactory factory;

    public DeliveryService(DeliveryPolicyFactory factory) {
        this.factory = Objects.requireNonNull(factory, "Фабрика политик доставки не задана");
    }

    public double calculate(DeliveryRequest request) {
        Objects.requireNonNull(request, "Запрос на доставку не может быть null");
        DeliveryPolicy policy = factory.get(request.type());
        if (!policy.supports(request)) {
            throw new IllegalArgumentException(
                "Способ доставки " + request.type() + " недоступен для параметров заказа: " + request
            );
        }
        return policy.calculateCost(request);
    }
}
