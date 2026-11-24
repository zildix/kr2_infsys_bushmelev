package ru.edu.pr07;

public class HeavyCargoPolicy implements DeliveryPolicy {
    // Параметр варианта 6: минимальный вес для крупногабаритной доставки (35 кг)
    public static final double MIN_WEIGHT_KG = 35.0;
    public static final double MAX_WEIGHT_KG = 1000.0;
    public static final double BASE_COST = 1200.0;
    public static final double KM_PRICE = 28.0;
    public static final double KG_PRICE = 12.0;

    @Override
    public boolean supports(DeliveryRequest request) {
        return request != null
                && request.weightKg() >= MIN_WEIGHT_KG
                && request.weightKg() <= MAX_WEIGHT_KG;
    }

    @Override
    public double calculateCost(DeliveryRequest request) {
        return BASE_COST
                + request.distanceKm() * KM_PRICE
                + request.weightKg() * KG_PRICE;
    }
}
