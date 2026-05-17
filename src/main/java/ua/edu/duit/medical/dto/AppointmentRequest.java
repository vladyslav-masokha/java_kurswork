package ua.edu.duit.medical.dto;

public final class AppointmentRequest {
    private final String doctorId;
    private final String slotId;
    private final String patientName;
    private final String phone;
    private final String email;
    private final String notes;

    public AppointmentRequest(String doctorId, String slotId, String patientName, String phone, String email, String notes) {
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.patientName = patientName;
        this.phone = phone;
        this.email = email;
        this.notes = notes;
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
}

