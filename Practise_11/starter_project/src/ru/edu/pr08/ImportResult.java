package ru.edu.pr08;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
    private int accepted;
    private final List<ImportError> errors = new ArrayList<>();

    public void addAccepted() {
        accepted++;
    }

    public void addError(ImportError error) {
        errors.add(error);
    }

    public int accepted() {
        return accepted;
    }

    public int rejected() {
        return errors.size();
    }

    public List<ImportError> errors() {
        return List.copyOf(errors);
    }
}
