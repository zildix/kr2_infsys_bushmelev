package ru.edu.pr06;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvStockLoader {
    public void loadInto(Warehouse warehouse, Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(";");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Неверный формат строки CSV: " + line);
            }
            String code = parts[0].trim();
            String name = parts[1].trim();
            Category category = Category.valueOf(parts[2].trim());
            int quantity = Integer.parseInt(parts[3].trim());
            warehouse.addItem(new StockItem(code, name, category, quantity));
        }
    }
}
