package ua.edu.duit.medical.domain;

import java.time.LocalDateTime;

public final class Appointment {
    private final String id;
    private final String doctorId;
    private final String slotId;
    private final String patientName;
    private final String phone;
    private final String email;
    private final String notes;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final LocalDateTime createdAt;
    private AppointmentStatus status;

    public Appointment(String id, String doctorId, String slotId, String patientName, String phone, String email, String notes,
            LocalDateTime startsAt, LocalDateTime endsAt, LocalDateTime createdAt, AppointmentStatus status) {
        this.id = id;
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.patientName = patientName;
        this.phone = phone;
        this.email = email;
        this.notes = notes;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }

    public boolean isActive() {
        return status == AppointmentStatus.SCHEDULED;
    }
}

