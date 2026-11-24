package ru.edu.pr08;

import java.util.ArrayList;
import java.util.List;

public class ClientValidator {
    // Параметр варианта 4: обязательное поле name (не пустое, верхний порог длины 25 символов)
    public static final int MAX_NAME_LENGTH = 25;

    public List<String> validate(ClientDraft draft) {
        List<String> errors = new ArrayList<>();
        if (draft == null) {
            errors.add("Черновик клиента отсутствует");
            return errors;
        }

        validateInn(draft.inn(), errors);
        validateName(draft.name(), errors);
        validateEmail(draft.email(), errors);

        return errors;
    }

    private void validateInn(String inn, List<String> errors) {
        if (inn == null || inn.isBlank()) {
            errors.add("ИНН не заполнен");
            return;
        }
        if (!inn.matches("\\d{10}|\\d{12}")) {
            errors.add("ИНН должен содержать ровно 10 или 12 цифр");
        }
    }

    private void validateName(String name, List<String> errors) {
        if (name == null || name.isBlank()) {
            errors.add("Обязательное поле наименования клиента (name) не заполнено");
            return;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            errors.add("Наименование превышает допустимую длину (" + MAX_NAME_LENGTH + " символов)");
        }
    }

    private void validateEmail(String email, List<String> errors) {
        if (email == null || email.isBlank()) {
            errors.add("Email не заполнен");
            return;
        }
        int atIndex = email.indexOf('@');
        int lastAtIndex = email.lastIndexOf('@');
        if (atIndex <= 0 || atIndex != lastAtIndex || atIndex == email.length() - 1) {
            errors.add("Некорректный email (должен содержать один '@' с непустыми частями слева и справа)");
        }
    }
}
