package ua.edu.duit.medical.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ua.edu.duit.medical.domain.Appointment;

public final class AppointmentRepository {
    private final Map<String, Appointment> items = new LinkedHashMap<String, Appointment>();

    public synchronized Appointment save(Appointment appointment) {
        items.put(appointment.getId(), appointment);
        return appointment;
    }

    public synchronized Appointment findById(String id) {
        return items.get(id);
    }

    public synchronized List<Appointment> findAll() {
        return new ArrayList<Appointment>(items.values());
    }

    public synchronized List<Appointment> findByPhone(String phone) {
        List<Appointment> result = new ArrayList<Appointment>();
        for (Appointment appointment : items.values()) {
            if (appointment.getPhone().equals(phone)) {
                result.add(appointment);
            }
        }
        return result;
    }

    public synchronized boolean hasActiveAppointmentForSlot(String doctorId, String slotId) {
        for (Appointment appointment : items.values()) {
            if (appointment.isActive()
                    && appointment.getDoctorId().equals(doctorId)
                    && appointment.getSlotId().equals(slotId)) {
                return true;
            }
        }
        return false;
    }
}

