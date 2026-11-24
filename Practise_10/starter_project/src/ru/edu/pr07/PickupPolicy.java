package ru.edu.pr07;

public class PickupPolicy implements DeliveryPolicy {
    public static final double MAX_WEIGHT_KG = 30.0;

    @Override
    public boolean supports(DeliveryRequest request) {
        return request != null && request.weightKg() <= MAX_WEIGHT_KG;
    }

    @Override
    public double calculateCost(DeliveryRequest request) {
        return 0.0;
    }
}
