package ua.edu.duit.medical.config;

import ua.edu.duit.medical.repository.AppointmentRepository;
import ua.edu.duit.medical.repository.DoctorRepository;
import ua.edu.duit.medical.repository.SpecialtyRepository;
import ua.edu.duit.medical.service.AppointmentService;
import ua.edu.duit.medical.service.DoctorService;
import ua.edu.duit.medical.web.ApiHandler;
import ua.edu.duit.medical.web.ApiServer;

public final class ApplicationFactory {
    private ApplicationFactory() {
    }

    public static ApiServer createServer(int port) {
        SpecialtyRepository specialtyRepository = new SpecialtyRepository();
        DoctorRepository doctorRepository = new DoctorRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        DataSeeder.seed(specialtyRepository, doctorRepository);

        DoctorService doctorService = new DoctorService(specialtyRepository, doctorRepository, appointmentRepository);
        AppointmentService appointmentService = new AppointmentService(doctorRepository, appointmentRepository);
        ApiHandler handler = new ApiHandler(doctorService, appointmentService);

        return new ApiServer(port, handler);
    }
}

