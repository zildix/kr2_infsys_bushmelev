package ru.edu.pr07;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvDeliveryLoader {
    public List<DeliveryRequest> load(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        List<DeliveryRequest> result = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(";");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Некорректная строка в CSV: " + line);
            }
            DeliveryType type = DeliveryType.valueOf(parts[0].trim());
            double distanceKm = Double.parseDouble(parts[1].trim());
            double weightKg = Double.parseDouble(parts[2].trim());
            double orderAmount = Double.parseDouble(parts[3].trim());
            result.add(new DeliveryRequest(type, distanceKm, weightKg, orderAmount));
        }
        return result;
    }
}
