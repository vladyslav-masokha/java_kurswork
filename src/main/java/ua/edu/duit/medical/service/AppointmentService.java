package ua.edu.duit.medical.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import ua.edu.duit.medical.domain.Appointment;
import ua.edu.duit.medical.domain.AppointmentStatus;
import ua.edu.duit.medical.domain.AvailabilitySlot;
import ua.edu.duit.medical.domain.Doctor;
import ua.edu.duit.medical.dto.AppointmentRequest;
import ua.edu.duit.medical.exception.ConflictException;
import ua.edu.duit.medical.exception.NotFoundException;
import ua.edu.duit.medical.exception.ValidationException;
import ua.edu.duit.medical.repository.AppointmentRepository;
import ua.edu.duit.medical.repository.DoctorRepository;
import ua.edu.duit.medical.util.Strings;

public final class AppointmentService {
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;

    public AppointmentService(DoctorRepository doctors, AppointmentRepository appointments) {
        this.doctors = doctors;
        this.appointments = appointments;
    }

    public Appointment create(AppointmentRequest request) {
        validate(request);

        Doctor doctor = doctors.findById(request.getDoctorId());
        if (doctor == null) {
            throw new NotFoundException("Лікаря не знайдено.");
        }

        AvailabilitySlot slot = findSlot(doctor, request.getSlotId());
        if (slot == null) {
            throw new NotFoundException("Обраний часовий слот не знайдено.");
        }
        if (slot.getStartsAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неможливо записатися на минулий час.");
        }
        if (appointments.hasActiveAppointmentForSlot(doctor.getId(), slot.getId())) {
            throw new ConflictException("Цей часовий слот вже зайнятий.");
        }

        Appointment appointment = new Appointment(
                "apt-" + UUID.randomUUID().toString().substring(0, 8),
                doctor.getId(),
                slot.getId(),
                request.getPatientName().trim(),
                request.getPhone().trim(),
                normalizeOptional(request.getEmail()),
                normalizeOptional(request.getNotes()),
                slot.getStartsAt(),
                slot.getEndsAt(),
                LocalDateTime.now(),
                AppointmentStatus.SCHEDULED);

        return appointments.save(appointment);
    }

    public List<Appointment> findByPhone(String phone) {
        if (Strings.isBlank(phone)) {
            throw new ValidationException("Параметр phone є обов'язковим.");
        }
        return appointments.findByPhone(phone.trim());
    }

    public Appointment cancel(String appointmentId) {
        Appointment appointment = appointments.findById(appointmentId);
        if (appointment == null) {
            throw new NotFoundException("Запис не знайдено.");
        }
        appointment.cancel();
        return appointment;
    }

    private void validate(AppointmentRequest request) {
        if (request == null) {
            throw new ValidationException("Тіло запиту є обов'язковим.");
        }
        if (Strings.isBlank(request.getDoctorId())) {
            throw new ValidationException("Поле doctorId є обов'язковим.");
        }
        if (Strings.isBlank(request.getSlotId())) {
            throw new ValidationException("Поле slotId є обов'язковим.");
        }
        if (Strings.isBlank(request.getPatientName())) {
            throw new ValidationException("Поле patientName є обов'язковим.");
        }
        if (Strings.isBlank(request.getPhone())) {
            throw new ValidationException("Поле phone є обов'язковим.");
        }
        if (!request.getPhone().trim().matches("\\+?[0-9 ]{10,16}")) {
            throw new ValidationException("Телефон має містити 10-16 цифр і може починатися з '+'.");
        }
        if (!Strings.isBlank(request.getEmail()) && !request.getEmail().contains("@")) {
            throw new ValidationException("Email має некоректний формат.");
        }
    }

    private AvailabilitySlot findSlot(Doctor doctor, String slotId) {
        for (AvailabilitySlot slot : doctor.getAvailability()) {
            if (slot.getId().equals(slotId)) {
                return slot;
            }
        }
        return null;
    }

    private String normalizeOptional(String value) {
        return Strings.isBlank(value) ? "" : value.trim();
    }
}

