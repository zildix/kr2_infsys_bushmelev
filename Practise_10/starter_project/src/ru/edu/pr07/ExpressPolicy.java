package ru.edu.pr07;

public class ExpressPolicy implements DeliveryPolicy {
    public static final double MAX_WEIGHT_KG = 10.0;
    public static final double MAX_DISTANCE_KM = 25.0;
    public static final double BASE_COST = 500.0;
    public static final double KM_PRICE = 30.0;
    public static final double KG_PRICE = 20.0;

    @Override
    public boolean supports(DeliveryRequest request) {
        return request != null
                && request.weightKg() <= MAX_WEIGHT_KG
                && request.distanceKm() <= MAX_DISTANCE_KM;
    }

    @Override
    public double calculateCost(DeliveryRequest request) {
        return BASE_COST
                + request.distanceKm() * KM_PRICE
                + request.weightKg() * KG_PRICE;
    }
}
