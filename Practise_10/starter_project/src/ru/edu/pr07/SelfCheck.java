package ru.edu.pr07;

import java.nio.file.Path;
import java.util.List;

public class SelfCheck {
    public static void main(String[] args) {
        try {
            DeliveryService service = new DeliveryService(new DeliveryPolicyFactory());

            // 0. Проверка чтения исходного sample.csv
            Path samplePath = Path.of("data", "sample.csv");
            if (samplePath.toFile().exists()) {
                List<DeliveryRequest> sample = new CsvDeliveryLoader().load(samplePath);
                assertCondition(sample.size() == 3, "В sample.csv должно быть 3 записи");
                assertEquals(0.0, service.calculate(sample.get(0)), "STANDARD от 10000 должен быть бесплатным");
                assertEquals(674.0, service.calculate(sample.get(1)), "EXPRESS(5 км, 1.2 кг) должен стоить 674.0");
                assertEquals(0.0, service.calculate(sample.get(2)), "PICKUP должен стоить 0.0");
            }

            // 1. Типовое значение: STANDARD (10 км, 5 кг, заказ 4000 -> 250 + 150 = 400)
            assertEquals(400.0,
                service.calculate(new DeliveryRequest(DeliveryType.STANDARD, 10, 5, 4000)),
                "Ошибка расчёта типового STANDARD");

            // 2. Нижняя граница по варианту 6 (HEAVY_CARGO: ровно 35.0 кг допускается, 34.9 кг отклоняется)
            assertEquals(1900.0,
                service.calculate(new DeliveryRequest(DeliveryType.HEAVY_CARGO, 10, 35.0, 25000)),
                "HEAVY_CARGO с весом ровно 35 кг должен проходить (1200 + 280 + 420 = 1900)");

            boolean heavyUnderweightRejected = false;
            try {
                service.calculate(new DeliveryRequest(DeliveryType.HEAVY_CARGO, 10, 34.9, 25000));
            } catch (IllegalArgumentException expected) {
                heavyUnderweightRejected = true;
            }
            assertCondition(heavyUnderweightRejected, "HEAVY_CARGO легче 35 кг должен отклоняться");

            // 3. Верхняя граница для EXPRESS (вес ровно 10.0 кг проходит, 10.1 кг отклоняется)
            assertEquals(850.0,
                service.calculate(new DeliveryRequest(DeliveryType.EXPRESS, 5, 10.0, 2500)),
                "EXPRESS с весом ровно 10 кг должен проходить");

            boolean expressOverweightRejected = false;
            try {
                service.calculate(new DeliveryRequest(DeliveryType.EXPRESS, 5, 10.1, 2500));
            } catch (IllegalArgumentException expected) {
                expressOverweightRejected = true;
            }
            assertCondition(expressOverweightRejected, "EXPRESS тяжелее 10 кг должен отклоняться");

            // 4. Пустое / отсутствующее значение (null-запрос)
            boolean nullRejected = false;
            try {
                service.calculate(null);
            } catch (NullPointerException expected) {
                nullRejected = true;
            }
            assertCondition(nullRejected, "Null-запрос должен вызывать NullPointerException");

            // 5. Неверный формат входных данных (нулевой вес заказа)
            boolean zeroWeightRejected = false;
            try {
                new DeliveryRequest(DeliveryType.STANDARD, 5, 0, 1500);
            } catch (IllegalArgumentException expected) {
                zeroWeightRejected = true;
            }
            assertCondition(zeroWeightRejected, "Нулевой вес должен блокироваться при создании запроса");

            // 6. Конфликт параметров: самовывоз PICKUP с весом 35 кг при лимите 30 кг
            boolean pickupConflictRejected = false;
            try {
                service.calculate(new DeliveryRequest(DeliveryType.PICKUP, 0, 35.0, 7000));
            } catch (IllegalArgumentException expected) {
                pickupConflictRejected = true;
            }
            assertCondition(pickupConflictRejected, "Самовывоз тяжелее 30 кг должен отклоняться");

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 1e-6) {
            throw new AssertionError(message + " (ожидалось " + expected + ", получено " + actual + ")");
        }
    }
}
