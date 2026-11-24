package ru.edu.pr07;

public class StandardCourierPolicy implements DeliveryPolicy {
    public static final double MAX_WEIGHT_KG = 20.0;
    public static final double MAX_DISTANCE_KM = 50.0;
    public static final double FREE_DELIVERY_MIN_ORDER = 10000.0;
    public static final double BASE_COST = 250.0;
    public static final double KM_PRICE = 15.0;

    @Override
    public boolean supports(DeliveryRequest request) {
        return request != null
                && request.weightKg() <= MAX_WEIGHT_KG
                && request.distanceKm() <= MAX_DISTANCE_KM;
    }

    @Override
    public double calculateCost(DeliveryRequest request) {
        if (request.orderAmount() >= FREE_DELIVERY_MIN_ORDER) {
            return 0.0;
        }
        return BASE_COST + request.distanceKm() * KM_PRICE;
    }
}
