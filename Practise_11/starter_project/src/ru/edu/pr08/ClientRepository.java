package ru.edu.pr08;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientRepository {
    private final Map<String, Client> data = new LinkedHashMap<>();

    public void save(Client client) {
        data.put(client.inn(), client);
    }

    public Client findByInn(String inn) {
        return data.get(inn);
    }

    public boolean existsByInn(String inn) {
        return data.containsKey(inn);
    }

    public int count() {
        return data.size();
    }

    public Map<String, Client> findAll() {
        return Collections.unmodifiableMap(data);
    }
}
