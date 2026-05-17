package ua.edu.duit.medical.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ua.edu.duit.medical.domain.Doctor;

public final class DoctorRepository {
    private final Map<String, Doctor> items = new LinkedHashMap<String, Doctor>();

    public synchronized Doctor save(Doctor doctor) {
        items.put(doctor.getId(), doctor);
        return doctor;
    }

    public synchronized Doctor findById(String id) {
        return items.get(id);
    }

    public synchronized List<Doctor> findAll() {
        return new ArrayList<Doctor>(items.values());
    }
}

