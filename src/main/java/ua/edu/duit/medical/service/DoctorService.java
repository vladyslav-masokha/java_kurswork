package ua.edu.duit.medical.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ua.edu.duit.medical.domain.Appointment;
import ua.edu.duit.medical.domain.AvailabilitySlot;
import ua.edu.duit.medical.domain.Doctor;
import ua.edu.duit.medical.domain.Specialty;
import ua.edu.duit.medical.dto.DoctorSearchCriteria;
import ua.edu.duit.medical.exception.NotFoundException;
import ua.edu.duit.medical.repository.AppointmentRepository;
import ua.edu.duit.medical.repository.DoctorRepository;
import ua.edu.duit.medical.repository.SpecialtyRepository;
import ua.edu.duit.medical.util.Strings;

public final class DoctorService {
    private final SpecialtyRepository specialties;
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;

    public DoctorService(SpecialtyRepository specialties, DoctorRepository doctors, AppointmentRepository appointments) {
        this.specialties = specialties;
        this.doctors = doctors;
        this.appointments = appointments;
    }

    public List<Specialty> getSpecialties() {
        return specialties.findAll();
    }

    public Doctor getDoctor(String doctorId) {
        Doctor doctor = doctors.findById(doctorId);
        if (doctor == null) {
            throw new NotFoundException("Лікаря не знайдено.");
        }
        return doctor;
    }

    public Specialty getSpecialty(String specialtyId) {
        Specialty specialty = specialties.findById(specialtyId);
        if (specialty == null) {
            throw new NotFoundException("Спеціалізацію не знайдено.");
        }
        return specialty;
    }

    public List<Doctor> search(DoctorSearchCriteria criteria) {
        List<Doctor> result = new ArrayList<Doctor>();
        for (Doctor doctor : doctors.findAll()) {
            if (matches(doctor, criteria)) {
                result.add(doctor);
            }
        }
        Collections.sort(result, new Comparator<Doctor>() {
            public int compare(Doctor left, Doctor right) {
                return compareDoctors(left, right, criteria);
            }
        });
        return result;
    }

    public List<AvailabilitySlot> getFreeSlots(String doctorId, LocalDate date) {
        Doctor doctor = getDoctor(doctorId);
        List<AvailabilitySlot> result = new ArrayList<AvailabilitySlot>();
        for (AvailabilitySlot slot : doctor.getAvailability()) {
            if (isFutureFreeSlot(doctor, slot) && slot.isOnDate(date)) {
                result.add(slot);
            }
        }
        return result;
    }

    public boolean hasFreeSlotOnDate(Doctor doctor, LocalDate date) {
        for (AvailabilitySlot slot : doctor.getAvailability()) {
            if (isFutureFreeSlot(doctor, slot) && slot.isOnDate(date)) {
                return true;
            }
        }
        return false;
    }

    public Appointment findLatestAppointment(String doctorId) {
        Appointment latest = null;
        for (Appointment appointment : appointments.findAll()) {
            if (appointment.getDoctorId().equals(doctorId)) {
                latest = appointment;
            }
        }
        return latest;
    }

    private boolean matches(Doctor doctor, DoctorSearchCriteria criteria) {
        if (criteria == null) {
            return true;
        }
        if (!Strings.isBlank(criteria.getSpecialtyId()) && !doctor.getSpecialtyId().equalsIgnoreCase(criteria.getSpecialtyId())) {
            return false;
        }
        if (!Strings.isBlank(criteria.getCity()) && !doctor.getCity().equalsIgnoreCase(criteria.getCity())) {
            return false;
        }
        if (criteria.getMinRating() != null && doctor.getRating() < criteria.getMinRating().doubleValue()) {
            return false;
        }
        if (criteria.getMaxPrice() != null && doctor.getConsultationPrice().compareTo(criteria.getMaxPrice()) > 0) {
            return false;
        }
        if (criteria.getMinExperience() != null && doctor.getYearsExperience() < criteria.getMinExperience().intValue()) {
            return false;
        }
        if (criteria.getDate() != null && !hasFreeSlotOnDate(doctor, criteria.getDate())) {
            return false;
        }
        if (!Strings.isBlank(criteria.getQuery())) {
            String query = criteria.getQuery().toLowerCase();
            String haystack = (doctor.getFullName() + " " + doctor.getClinic() + " " + doctor.getCity()).toLowerCase();
            if (!haystack.contains(query)) {
                return false;
            }
        }
        return true;
    }

    private int compareDoctors(Doctor left, Doctor right, DoctorSearchCriteria criteria) {
        String sort = criteria == null ? null : criteria.getSort();
        if ("priceAsc".equals(sort)) {
            int price = left.getConsultationPrice().compareTo(right.getConsultationPrice());
            return price != 0 ? price : Double.compare(right.getRating(), left.getRating());
        }
        if ("experienceDesc".equals(sort)) {
            int experience = Integer.compare(right.getYearsExperience(), left.getYearsExperience());
            return experience != 0 ? experience : Double.compare(right.getRating(), left.getRating());
        }
        if ("nearest".equals(sort)) {
            LocalDateTime leftSlot = firstFreeSlot(left);
            LocalDateTime rightSlot = firstFreeSlot(right);
            if (leftSlot == null && rightSlot == null) {
                return Double.compare(right.getRating(), left.getRating());
            }
            if (leftSlot == null) {
                return 1;
            }
            if (rightSlot == null) {
                return -1;
            }
            int nearest = leftSlot.compareTo(rightSlot);
            return nearest != 0 ? nearest : Double.compare(right.getRating(), left.getRating());
        }
        return Double.compare(right.getRating(), left.getRating());
    }

    private LocalDateTime firstFreeSlot(Doctor doctor) {
        LocalDateTime first = null;
        for (AvailabilitySlot slot : doctor.getAvailability()) {
            if (isFutureFreeSlot(doctor, slot) && (first == null || slot.getStartsAt().isBefore(first))) {
                first = slot.getStartsAt();
            }
        }
        return first;
    }

    private boolean isFutureFreeSlot(Doctor doctor, AvailabilitySlot slot) {
        return !slot.getStartsAt().isBefore(LocalDateTime.now())
                && !appointments.hasActiveAppointmentForSlot(doctor.getId(), slot.getId());
    }
}
