package ru.edu.pr09;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvSalesLoader {
    public List<Sale> load(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        List<Sale> sales = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(";");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Некорректная строка CSV: " + line);
            }
            sales.add(new Sale(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                Double.parseDouble(parts[3].trim())
            ));
        }
        return List.copyOf(sales);
    }
}
