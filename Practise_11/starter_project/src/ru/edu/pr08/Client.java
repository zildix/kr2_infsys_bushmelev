package ru.edu.pr08;

import java.util.Objects;

public record Client(String inn, String name, String email) {
    public Client {
        Objects.requireNonNull(inn, "ИНН не может быть null");
        Objects.requireNonNull(name, "Наименование не может быть null");
        Objects.requireNonNull(email, "Email не может быть null");
    }
}
