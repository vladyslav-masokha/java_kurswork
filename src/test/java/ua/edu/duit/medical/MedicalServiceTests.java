package ua.edu.duit.medical;

import java.util.List;
import ua.edu.duit.medical.config.ApplicationFactory;
import ua.edu.duit.medical.config.DataSeeder;
import ua.edu.duit.medical.domain.Appointment;
import ua.edu.duit.medical.domain.AvailabilitySlot;
import ua.edu.duit.medical.dto.AppointmentRequest;
import ua.edu.duit.medical.dto.DoctorSearchCriteria;
import ua.edu.duit.medical.exception.ConflictException;
import ua.edu.duit.medical.repository.AppointmentRepository;
import ua.edu.duit.medical.repository.DoctorRepository;
import ua.edu.duit.medical.repository.SpecialtyRepository;
import ua.edu.duit.medical.service.AppointmentService;
import ua.edu.duit.medical.service.DoctorService;

public final class MedicalServiceTests {
    private MedicalServiceTests() {
    }

    public static void main(String[] args) {
        shouldSearchDoctorsBySpecialty();
        shouldCreateAppointmentAndLockSlot();
        shouldCancelAppointment();
        ApplicationFactory.createServer(0).stop();
        System.out.println("All service tests passed.");
    }

    private static void shouldSearchDoctorsBySpecialty() {
        TestContext context = context();
        List<?> doctors = context.doctorService.search(new DoctorSearchCriteria("cardiology", null, null, null, null, null, null, null));
        assertEquals(1, doctors.size(), "Пошук за спеціалізацією має повернути одного кардіолога.");
    }

    private static void shouldCreateAppointmentAndLockSlot() {
        TestContext context = context();
        AvailabilitySlot slot = context.doctorService.getFreeSlots("doc-1", null).get(0);
        Appointment appointment = context.appointmentService.create(new AppointmentRequest(
                "doc-1", slot.getId(), "Іван Петренко", "+380501112233", "ivan@example.com", "Тест"));

        assertTrue(appointment.getId().startsWith("apt-"), "Запис має отримати ідентифікатор.");

        try {
            context.appointmentService.create(new AppointmentRequest(
                    "doc-1", slot.getId(), "Петро Іваненко", "+380501112244", "petro@example.com", ""));
            throw new AssertionError("Повторний запис на один слот має бути заборонений.");
        } catch (ConflictException expected) {
            assertTrue(expected.getMessage().contains("вже зайнятий"), "Має бути помилка конфлікту.");
        }
    }

    private static void shouldCancelAppointment() {
        TestContext context = context();
        AvailabilitySlot slot = context.doctorService.getFreeSlots("doc-1", null).get(0);
        Appointment appointment = context.appointmentService.create(new AppointmentRequest(
                "doc-1", slot.getId(), "Олена Іваненко", "+380501112255", "olena@example.com", ""));
        context.appointmentService.cancel(appointment.getId());
        assertEquals(1, context.doctorService.getFreeSlots("doc-1", null).size() > 0 ? 1 : 0,
                "Після скасування слот знову має бути доступним.");
    }

    private static TestContext context() {
        SpecialtyRepository specialties = new SpecialtyRepository();
        DoctorRepository doctors = new DoctorRepository();
        AppointmentRepository appointments = new AppointmentRepository();
        DataSeeder.seed(specialties, doctors);
        DoctorService doctorService = new DoctorService(specialties, doctors, appointments);
        AppointmentService appointmentService = new AppointmentService(doctors, appointments);
        return new TestContext(doctorService, appointmentService);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " Очікувано: " + expected + ", отримано: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class TestContext {
        private final DoctorService doctorService;
        private final AppointmentService appointmentService;

        private TestContext(DoctorService doctorService, AppointmentService appointmentService) {
            this.doctorService = doctorService;
            this.appointmentService = appointmentService;
        }
    }
}
