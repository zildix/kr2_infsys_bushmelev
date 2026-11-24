package ru.edu.pr06;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class SelfCheck {
    public static void main(String[] args) {
        try {
            Warehouse warehouse = new Warehouse();
            CsvStockLoader loader = new CsvStockLoader();
            Path samplePath = Path.of("data", "sample.csv");
            if (samplePath.toFile().exists()) {
                loader.loadInto(warehouse, samplePath);
            } else {
                warehouse.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 12));
                warehouse.addItem(new StockItem("MN-01", "Monitor", Category.DISPLAY, 7));
                warehouse.addItem(new StockItem("KB-01", "Keyboard", Category.PERIPHERAL, 20));
            }

            // 1. Типовой приход и списание до точной нижней границы резерва (MIN_RESERVE = 10)
            warehouse.receive("NB-01", 3); // 12 + 3 = 15
            warehouse.issue("NB-01", 5);   // 15 - 5 = 10 (ровно MIN_RESERVE — допустимо)
            assertCondition(warehouse.findByCode("NB-01").orElseThrow().getQuantity() == 10,
                "Остаток NB-01 должен быть равен 10");

            // 2. Попытка уйти ниже минимального резерва (10 - 1 = 9 < 10)
            boolean reserveBlocked = false;
            try {
                warehouse.issue("NB-01", 1);
            } catch (IllegalStateException expected) {
                reserveBlocked = true;
            }
            assertCondition(reserveBlocked, "Списание ниже минимального резерва 10 должно блокироваться");

            // 3. Попытка добавления дубликата кода
            boolean duplicateBlocked = false;
            try {
                warehouse.addItem(new StockItem("KB-01", "Duplicate Keyboard", Category.PERIPHERAL, 15));
            } catch (IllegalArgumentException expected) {
                duplicateBlocked = true;
            }
            assertCondition(duplicateBlocked, "Повторный инвентарный код должен отклоняться");

            // 4. Пустое или отсутствующее значение кода и отрицательное количество
            boolean missingBlocked = false;
            try {
                warehouse.receive("UNKNOWN-99", 5);
            } catch (NoSuchElementException expected) {
                missingBlocked = true;
            }
            assertCondition(missingBlocked, "Неизвестный код должен вызывать исключение");

            boolean negativeBlocked = false;
            try {
                warehouse.receive("KB-01", 0);
            } catch (IllegalArgumentException expected) {
                negativeBlocked = true;
            }
            assertCondition(negativeBlocked, "Нулевое/отрицательное количество должно отклоняться");

            // 5. Изоляция возвращаемой коллекции findByCategory
            List<StockItem> peripherals = warehouse.findByCategory(Category.PERIPHERAL);
            peripherals.clear();
            assertCondition(warehouse.findByCategory(Category.PERIPHERAL).size() == 1,
                "Изменение внешнего списка не должно менять состояние склада");

            // 6. Проверка Set уникальных категорий и сводки totalsByCategory
            Set<Category> present = warehouse.getPresentCategories();
            assertCondition(present.size() == 3 && present.contains(Category.COMPUTER),
                "Должно быть 3 уникальные категории");

            Map<Category, Integer> totals = warehouse.totalsByCategory();
            assertCondition(totals.get(Category.COMPUTER) == 10 && totals.get(Category.PERIPHERAL) == 20,
                "Сводка по категориям рассчитана неверно");

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
}
