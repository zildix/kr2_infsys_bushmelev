package ru.edu.pr07;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class DeliveryPolicyFactory {
    private final Map<DeliveryType, DeliveryPolicy> policies = new EnumMap<>(DeliveryType.class);

    public DeliveryPolicyFactory() {
        policies.put(DeliveryType.STANDARD, new StandardCourierPolicy());
        policies.put(DeliveryType.EXPRESS, new ExpressPolicy());
        policies.put(DeliveryType.PICKUP, new PickupPolicy());
        policies.put(DeliveryType.HEAVY_CARGO, new HeavyCargoPolicy());
    }

    public DeliveryPolicy get(DeliveryType type) {
        Objects.requireNonNull(type, "Способ доставки не может быть null");
        DeliveryPolicy policy = policies.get(type);
        if (policy == null) {
            throw new IllegalArgumentException("Не поддерживается тип доставки: " + type);
        }
        return policy;
    }
}
