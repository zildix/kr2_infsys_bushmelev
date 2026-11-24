package ru.edu.pr08;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ClientImporter {
    private static final String EXPECTED_HEADER = "inn;name;email";

    private final ClientCsvParser parser;
    private final ClientValidator validator;
    private final ClientRepository repository;
    private final DuplicatePolicy duplicatePolicy;

    public ClientImporter(ClientRepository repository) {
        this(new ClientCsvParser(), new ClientValidator(), repository, DuplicatePolicy.REJECT);
    }

    public ClientImporter(ClientCsvParser parser,
                          ClientValidator validator,
                          ClientRepository repository,
                          DuplicatePolicy duplicatePolicy) {
        this.parser = Objects.requireNonNull(parser, "Парсер не задан");
        this.validator = Objects.requireNonNull(validator, "Валидатор не задан");
        this.repository = Objects.requireNonNull(repository, "Репозиторий не задан");
        this.duplicatePolicy = Objects.requireNonNull(duplicatePolicy, "Политика дубликатов не задана");
    }

    public ImportResult importFile(Path path) throws IOException {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        ImportResult result = new ImportResult();

        // Ошибка открытия файла (IOException) не перехватывается здесь и завершает импорт целиком
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Файл пуст и не содержит заголовка");
            }
            if (!EXPECTED_HEADER.equalsIgnoreCase(header.trim())) {
                throw new IOException("Неверный заголовок CSV: ожидалось '" + EXPECTED_HEADER + "', получено '" + header + "'");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                processSingleLine(line, lineNumber, result);
            }
        }
        return result;
    }

    private void processSingleLine(String line, int lineNumber, ImportResult result) {
        try {
            ClientDraft draft = parser.parse(line, lineNumber);
            List<String> validationErrors = validator.validate(draft);
            if (!validationErrors.isEmpty()) {
                result.addError(new ImportError(lineNumber, String.join("; ", validationErrors)));
                return;
            }

            if (repository.existsByInn(draft.inn())) {
                handleDuplicate(draft, lineNumber, result);
                return;
            }

            repository.save(new Client(draft.inn(), draft.name(), draft.email()));
            result.addAccepted();
        } catch (IllegalArgumentException e) {
            result.addError(new ImportError(lineNumber, e.getMessage()));
        }
    }

    private void handleDuplicate(ClientDraft draft, int lineNumber, ImportResult result) {
        switch (duplicatePolicy) {
            case REJECT -> result.addError(new ImportError(lineNumber, "Дубликат ИНН: " + draft.inn()));
            case OVERWRITE -> {
                repository.save(new Client(draft.inn(), draft.name(), draft.email()));
                result.addAccepted();
            }
            case SKIP -> {
                // Игнорируем повторную запись без ошибки
            }
        }
    }
}
