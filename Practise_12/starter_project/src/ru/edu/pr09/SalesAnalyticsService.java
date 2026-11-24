package ru.edu.pr09;

import java.util.*;
import java.util.stream.Collectors;

public class SalesAnalyticsService {
    // Параметр варианта 7: порог крупной сделки = 40_000 (модификатор 40 тыс.)
    public static final double LARGE_SALE_THRESHOLD = 40_000.0;

    // Компаратор с двумя полями сортировки: по убыванию суммы, при равенстве — по id по возрастанию
    private static final Comparator<Sale> BY_AMOUNT_DESC_THEN_ID =
            Comparator.comparingDouble(Sale::amount).reversed()
                      .thenComparing(Sale::id);

    public List<Sale> topSales(List<Sale> sales, double min, int limit) {
        validateSalesList(sales);
        if (min < 0) {
            throw new IllegalArgumentException("Минимальная сумма не может быть отрицательной: " + min);
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Лимит выборки должен быть положительным: " + limit);
        }

        return sales.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.amount() >= min)
                .sorted(BY_AMOUNT_DESC_THEN_ID)
                .limit(limit)
                .toList();
    }

    public List<Sale> largeSales(List<Sale> sales) {
        validateSalesList(sales);
        return sales.stream()
                .filter(Objects::nonNull)
                .filter(this::isLargeSale)
                .sorted(BY_AMOUNT_DESC_THEN_ID)
                .toList();
    }

    public SaleCategory classifySale(Sale sale) {
        Objects.requireNonNull(sale, "Сделка не может быть null");
        return isLargeSale(sale) ? SaleCategory.LARGE : SaleCategory.REGULAR;
    }

    public Optional<Sale> maxSale(List<Sale> sales) {
        validateSalesList(sales);
        return sales.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Sale::amount));
    }

    public Map<String, Double> totalsByManager(List<Sale> sales) {
        validateSalesList(sales);
        return sales.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Sale::manager,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Sale::amount)
                ));
    }

    public double averageByRegion(List<Sale> sales, String region) {
        validateSalesList(sales);
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Регион не может быть пустым");
        }
        return sales.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.region().equalsIgnoreCase(region.trim()))
                .mapToDouble(Sale::amount)
                .average()
                .orElse(0.0);
    }

    // Императивный аналог totalsByManager для сравнения читаемости в отчёте
    public Map<String, Double> totalsByManagerImperative(List<Sale> sales) {
        validateSalesList(sales);
        Map<String, Double> totals = new LinkedHashMap<>();
        for (Sale sale : sales) {
            if (sale != null) {
                totals.merge(sale.manager(), sale.amount(), Double::sum);
            }
        }
        return totals;
    }

    public boolean isLargeSale(Sale sale) {
        return sale != null && sale.amount() >= LARGE_SALE_THRESHOLD;
    }

    private void validateSalesList(List<Sale> sales) {
        Objects.requireNonNull(sales, "Список продаж не может быть null");
    }
}
