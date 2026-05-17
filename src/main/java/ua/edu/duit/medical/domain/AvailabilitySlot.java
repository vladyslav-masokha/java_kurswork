package ua.edu.duit.medical.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AvailabilitySlot {
    private final String id;
    private final String doctorId;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;

    public AvailabilitySlot(String id, String doctorId, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public String getId() {
        return id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public boolean isOnDate(LocalDate date) {
        return date == null || startsAt.toLocalDate().equals(date);
    }
}

