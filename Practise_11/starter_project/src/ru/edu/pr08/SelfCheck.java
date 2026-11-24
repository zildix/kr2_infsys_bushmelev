package ru.edu.pr08;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SelfCheck {
    public static void main(String[] args) {
        try {
            // 0. Проверка штатного sample.csv (2 валидные строки, 2 с ошибками: плохой ИНН и плохой email)
            Path samplePath = Path.of("data", "sample.csv");
            if (Files.exists(samplePath)) {
                ClientRepository repo = new ClientRepository();
                ClientImporter importer = new ClientImporter(repo);
                ImportResult res = importer.importFile(samplePath);
                assertCondition(res.accepted() == 2, "Из sample.csv должно приняться 2 записи");
                assertCondition(res.errors().size() == 2, "В sample.csv должно быть 2 ошибки");
                assertCondition(res.errors().get(0).lineNumber() == 3, "Первая ошибка должна быть на 3-й строке");
                assertCondition(res.errors().get(1).lineNumber() == 5, "Вторая ошибка должна быть на 5-й строке");
            }

            // 1. Типовое значение + граничные длины ИНН (ровно 10 и ровно 12 цифр) и границы длины name (1 и 25 символов)
            Path tempCsv = Files.createTempFile("clients_test_", ".csv");
            try {
                List<String> lines = List.of(
                    "inn;name;email",
                    "7701234567;A;a@b.ru",                                   // 2: нижняя граница (ИНН 10, имя 1 символ) -> OK
                    "781234567890;1234567890123456789012345;info@beta.com",  // 3: верхняя граница (ИНН 12, имя ровно 25) -> OK
                    "781234567891;12345678901234567890123456;long@beta.com", // 4: превышение верхней границы name (26 > 25) -> ERR
                    "7701112233;;empty@name.ru",                             // 5: пустое обязательное поле name (вариант 4) -> ERR
                    "123456789;Short Inn;short@inn.ru",                      // 6: ИНН 9 цифр -> ERR
                    "7701234567;Duplicate Alpha;dup@alpha.ru",               // 7: конфликт дубликата ИНН -> ERR
                    "broken_line_without_semicolons",                        // 8: неверный формат CSV -> ERR
                    "5001002003;Gamma Corp;user@gamma.org"                   // 9: корректная строка после ошибок -> OK
                );
                Files.write(tempCsv, lines, StandardCharsets.UTF_8);

                ClientRepository repo = new ClientRepository();
                ClientImporter importer = new ClientImporter(repo);
                ImportResult result = importer.importFile(tempCsv);

                assertCondition(result.accepted() == 3, "Ожидалось 3 принятых клиента, получено: " + result.accepted());
                assertCondition(result.errors().size() == 5, "Ожидалось 5 ошибок по строкам, получено: " + result.errors().size());
                assertCondition(repo.findByInn("5001002003") != null, "Импорт должен продолжаться после ошибочных строк");
            } finally {
                Files.deleteIfExists(tempCsv);
            }

            // 2. Ошибка отсутствующего файла должна пробрасывать IOException целиком
            boolean missingFileFailed = false;
            try {
                new ClientImporter(new ClientRepository()).importFile(Path.of("non_existent_file_12345.csv"));
            } catch (IOException expected) {
                missingFileFailed = true;
            }
            assertCondition(missingFileFailed, "Отсутствующий файл должен вызывать IOException");

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
