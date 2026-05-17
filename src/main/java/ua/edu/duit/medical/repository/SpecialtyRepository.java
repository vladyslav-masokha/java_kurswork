package ua.edu.duit.medical.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ua.edu.duit.medical.domain.Specialty;

public final class SpecialtyRepository {
    private final Map<String, Specialty> items = new LinkedHashMap<String, Specialty>();

    public synchronized Specialty save(Specialty specialty) {
        items.put(specialty.getId(), specialty);
        return specialty;
    }

    public synchronized Specialty findById(String id) {
        return items.get(id);
    }

    public synchronized List<Specialty> findAll() {
        return new ArrayList<Specialty>(items.values());
    }
}

