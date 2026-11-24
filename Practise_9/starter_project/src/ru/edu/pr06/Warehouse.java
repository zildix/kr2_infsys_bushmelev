package ru.edu.pr06;

import java.util.*;

public class Warehouse {
    private final Map<String, StockItem> items = new HashMap<>();
    private final List<StockMovement> movements = new ArrayList<>();

    public void addItem(StockItem item) {
        Objects.requireNonNull(item, "Карточка позиции не может быть null");
        if (items.containsKey(item.getCode())) {
            throw new IllegalArgumentException("Позиция с кодом " + item.getCode() + " уже зарегистрирована");
        }
        items.put(item.getCode(), item);
    }

    public void receive(String code, int q) {
        StockItem item = getExistingItem(code);
        item.increase(q);
        movements.add(new StockMovement(item.getCode(), MovementType.RECEIVE, q));
    }

    public void issue(String code, int q) {
        StockItem item = getExistingItem(code);
        item.decrease(q);
        movements.add(new StockMovement(item.getCode(), MovementType.ISSUE, q));
    }

    public List<StockItem> findByCategory(Category category) {
        Objects.requireNonNull(category, "Категория не может быть null");
        List<StockItem> result = new ArrayList<>();
        for (StockItem item : items.values()) {
            if (item.getCategory() == category) {
                result.add(item);
            }
        }
        return result;
    }

    public Map<Category, Integer> totalsByCategory() {
        Map<Category, Integer> totals = new EnumMap<>(Category.class);
        for (StockItem item : items.values()) {
            totals.merge(item.getCategory(), item.getQuantity(), Integer::sum);
        }
        return Collections.unmodifiableMap(totals);
    }

    public Set<Category> getPresentCategories() {
        Set<Category> categories = EnumSet.noneOf(Category.class);
        for (StockItem item : items.values()) {
            categories.add(item.getCategory());
        }
        return Collections.unmodifiableSet(categories);
    }

    public Set<String> getAllCodes() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(items.keySet()));
    }

    public List<StockMovement> getMovements() {
        return List.copyOf(movements);
    }

    public Optional<StockItem> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(items.get(code.trim()));
    }

    private StockItem getExistingItem(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Инвентарный код не должен быть пустым");
        }
        StockItem item = items.get(code.trim());
        if (item == null) {
            throw new NoSuchElementException("Позиция с кодом " + code + " не найдена на складе");
        }
        return item;
    }
}
