package ru.edu.pr09;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SelfCheck {
    public static void main(String[] args) {
        try {
            SalesAnalyticsService service = new SalesAnalyticsService();

            // 0. Проверка чтения исходного sample.csv
            Path samplePath = Path.of("data", "sample.csv");
            List<Sale> baseSales;
            if (Files.exists(samplePath)) {
                baseSales = new CsvSalesLoader().load(samplePath);
            } else {
                baseSales = List.of(
                    new Sale("S1", "Ivanov", "Moscow", 150000),
                    new Sale("S2", "Petrova", "SPb", 92000),
                    new Sale("S3", "Ivanov", "Moscow", 240000),
                    new Sale("S4", "Lee", "Kazan", 45000)
                );
            }
            List<Sale> snapshotBefore = new ArrayList<>(baseSales);

            // 1. Типовое значение: topSales, maxSale, totalsByManager, averageByRegion
            List<Sale> top2 = service.topSales(baseSales, 50000, 2);
            assertCondition(top2.size() == 2 && top2.get(0).id().equals("S3") && top2.get(1).id().equals("S1"),
                "topSales должен вернуть S3 (240000) и S1 (150000)");

            Optional<Sale> max = service.maxSale(baseSales);
            assertCondition(max.isPresent() && max.get().id().equals("S3"),
                "maxSale должен вернуть S3");

            Map<String, Double> totals = service.totalsByManager(baseSales);
            assertEquals(390000.0, totals.get("Ivanov"), "Сумма по Ivanov должна быть 390000");
            assertEquals(totals, service.totalsByManagerImperative(baseSales),
                "Stream-группировка и цикл должны давать одинаковый результат");

            assertEquals(195000.0, service.averageByRegion(baseSales, "Moscow"),
                "Среднее по Moscow должно быть 195000");

            // 2. Нижняя и верхняя границы варианта 7 (порог крупной сделки = 40000)
            Sale exactThreshold = new Sale("S5", "Smirnov", "Tula", 40000.0);
            Sale belowThreshold = new Sale("S6", "Smirnov", "Tula", 39999.99);
            assertCondition(service.classifySale(exactThreshold) == SaleCategory.LARGE,
                "Сделка ровно на 40000 должна считаться LARGE");
            assertCondition(service.classifySale(belowThreshold) == SaleCategory.REGULAR,
                "Сделка на 39999.99 должна считаться REGULAR");

            List<Sale> largeOnly = service.largeSales(List.of(exactThreshold, belowThreshold));
            assertCondition(largeOnly.size() == 1 && largeOnly.get(0).id().equals("S5"),
                "В выборку largeSales должна попасть только сделка >= 40000");

            // 3. Проверка вторичной сортировки Comparator при одинаковых суммах (по id по возрастанию)
            Sale tieB = new Sale("B-10", "Kim", "Omsk", 100000);
            Sale tieA = new Sale("A-10", "Kim", "Omsk", 100000);
            List<Sale> sortedTie = service.topSales(List.of(tieB, tieA), 10000, 2);
            assertCondition(sortedTie.get(0).id().equals("A-10") && sortedTie.get(1).id().equals("B-10"),
                "При равной сумме первым должен идти меньший id");

            // 4. Пустая коллекция -> Optional.empty() и 0.0 для среднего по отсутствующему региону
            assertCondition(service.maxSale(List.of()).isEmpty(), "Для пустого списка maxSale -> Optional.empty()");
            assertEquals(0.0, service.averageByRegion(baseSales, "UnknownCity"),
                "Для отсутствующего региона среднее должно быть 0.0");

            // 5. Неверный формат / недопустимые входные параметры (null-список, отрицательная сумма, limit <= 0)
            boolean nullRejected = false;
            try {
                service.topSales(null, 1000, 3);
            } catch (NullPointerException expected) {
                nullRejected = true;
            }
            assertCondition(nullRejected, "Null-список должен вызывать NullPointerException");

            boolean negativeSaleRejected = false;
            try {
                new Sale("BAD", "Test", "Moscow", -100);
            } catch (IllegalArgumentException expected) {
                negativeSaleRejected = true;
            }
            assertCondition(negativeSaleRejected, "Отрицательная сумма сделки должна отклоняться");

            // 6. Неизменность исходного списка после всех аналитических операций
            assertCondition(baseSales.equals(snapshotBefore),
                "Исходная коллекция продаж не должна изменяться после вызова сервисов");

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

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " (ожидалось: " + expected + ", получено: " + actual + ")");
        }
    }

    private static void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 1e-6) {
            throw new AssertionError(message + " (ожидалось: " + expected + ", получено: " + actual + ")");
        }
    }
}
