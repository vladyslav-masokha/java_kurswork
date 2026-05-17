package ua.edu.duit.medical.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ua.edu.duit.medical.domain.Appointment;
import ua.edu.duit.medical.domain.AvailabilitySlot;
import ua.edu.duit.medical.domain.Doctor;
import ua.edu.duit.medical.domain.Specialty;
import ua.edu.duit.medical.dto.AppointmentRequest;
import ua.edu.duit.medical.dto.DoctorSearchCriteria;
import ua.edu.duit.medical.exception.ApiException;
import ua.edu.duit.medical.exception.ValidationException;
import ua.edu.duit.medical.service.AppointmentService;
import ua.edu.duit.medical.service.DoctorService;
import ua.edu.duit.medical.util.Dates;
import ua.edu.duit.medical.util.Json;
import ua.edu.duit.medical.util.Strings;

public final class ApiHandler implements HttpHandler {
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public ApiHandler(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            Responses.send(exchange, 204, "");
            return;
        }
        try {
            route(exchange);
        } catch (ApiException ex) {
            Responses.json(exchange, ex.getStatusCode(), error(ex.getMessage()));
        } catch (Exception ex) {
            Responses.json(exchange, 500, error("Внутрішня помилка сервера: " + ex.getMessage()));
        }
    }

    private void route(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());

        if ("GET".equals(method) && "/health".equals(path)) {
            Map<String, Object> response = Json.object();
            response.put("status", "UP");
            response.put("service", "МедНавігатор API");
            Responses.json(exchange, 200, response);
            return;
        }

        if ("GET".equals(method) && "/api/specialties".equals(path)) {
            Responses.json(exchange, 200, specialtiesToJson(doctorService.getSpecialties()));
            return;
        }

        if ("GET".equals(method) && "/api/doctors".equals(path)) {
            DoctorSearchCriteria criteria = new DoctorSearchCriteria(
                    query.get("specialty"),
                    query.get("city"),
                    query.get("q"),
                    Dates.parseDate(query.get("date")),
                    parseDouble(query.get("minRating")),
                    parseMoney(query.get("maxPrice")),
                    parseInteger(query.get("minExperience"), "minExperience"),
                    query.get("sort"));
            Responses.json(exchange, 200, doctorsToJson(doctorService.search(criteria), true));
            return;
        }

        if ("GET".equals(method) && path.startsWith("/api/doctors/")) {
            handleDoctorRoutes(exchange, path, query);
            return;
        }

        if ("POST".equals(method) && "/api/appointments".equals(path)) {
            Map<String, Object> body = Json.parseObject(Requests.readBody(exchange));
            Appointment appointment = appointmentService.create(new AppointmentRequest(
                    string(body.get("doctorId")),
                    string(body.get("slotId")),
                    string(body.get("patientName")),
                    string(body.get("phone")),
                    string(body.get("email")),
                    string(body.get("notes"))));
            Responses.json(exchange, 201, appointmentToJson(appointment));
            return;
        }

        if ("GET".equals(method) && "/api/appointments".equals(path)) {
            Responses.json(exchange, 200, appointmentsToJson(appointmentService.findByPhone(query.get("phone"))));
            return;
        }

        if ("DELETE".equals(method) && path.startsWith("/api/appointments/")) {
            String appointmentId = lastSegment(path);
            Responses.json(exchange, 200, appointmentToJson(appointmentService.cancel(appointmentId)));
            return;
        }

        Responses.json(exchange, 404, error("Маршрут не знайдено."));
    }

    private void handleDoctorRoutes(HttpExchange exchange, String path, Map<String, String> query) throws IOException {
        String[] segments = path.split("/");
        if (segments.length < 4) {
            Responses.json(exchange, 404, error("Маршрут не знайдено."));
            return;
        }
        String doctorId = decode(segments[3]);
        if (segments.length == 4) {
            Responses.json(exchange, 200, doctorToJson(doctorService.getDoctor(doctorId), true));
            return;
        }
        if (segments.length == 5 && "slots".equals(segments[4])) {
            LocalDate date = Dates.parseDate(query.get("date"));
            Responses.json(exchange, 200, slotsToJson(doctorService.getFreeSlots(doctorId, date)));
            return;
        }
        Responses.json(exchange, 404, error("Маршрут не знайдено."));
    }

    private List<Object> specialtiesToJson(List<Specialty> specialties) {
        List<Object> result = Json.array();
        for (Specialty specialty : specialties) {
            result.add(specialtyToJson(specialty));
        }
        return result;
    }

    private Map<String, Object> specialtyToJson(Specialty specialty) {
        Map<String, Object> item = Json.object();
        item.put("id", specialty.getId());
        item.put("name", specialty.getName());
        item.put("description", specialty.getDescription());
        return item;
    }

    private List<Object> doctorsToJson(List<Doctor> doctors, boolean includeSlots) {
        List<Object> result = Json.array();
        for (Doctor doctor : doctors) {
            result.add(doctorToJson(doctor, includeSlots));
        }
        return result;
    }

    private Map<String, Object> doctorToJson(Doctor doctor, boolean includeSlots) {
        Specialty specialty = doctorService.getSpecialty(doctor.getSpecialtyId());
        Map<String, Object> item = Json.object();
        item.put("id", doctor.getId());
        item.put("fullName", doctor.getFullName());
        item.put("specialty", specialtyToJson(specialty));
        item.put("city", doctor.getCity());
        item.put("clinic", doctor.getClinic());
        item.put("address", doctor.getAddress());
        item.put("rating", doctor.getRating());
        item.put("yearsExperience", doctor.getYearsExperience());
        item.put("consultationPrice", money(doctor.getConsultationPrice()));
        item.put("languages", new ArrayList<String>(doctor.getLanguages()));
        if (includeSlots) {
            item.put("availableSlots", slotsToJson(doctorService.getFreeSlots(doctor.getId(), null)));
        }
        return item;
    }

    private List<Object> slotsToJson(List<AvailabilitySlot> slots) {
        List<Object> result = Json.array();
        for (AvailabilitySlot slot : slots) {
            Map<String, Object> item = Json.object();
            item.put("id", slot.getId());
            item.put("doctorId", slot.getDoctorId());
            item.put("startsAt", Dates.format(slot.getStartsAt()));
            item.put("endsAt", Dates.format(slot.getEndsAt()));
            result.add(item);
        }
        return result;
    }

    private List<Object> appointmentsToJson(List<Appointment> appointments) {
        List<Object> result = Json.array();
        for (Appointment appointment : appointments) {
            result.add(appointmentToJson(appointment));
        }
        return result;
    }

    private Map<String, Object> appointmentToJson(Appointment appointment) {
        Map<String, Object> item = Json.object();
        item.put("id", appointment.getId());
        item.put("doctorId", appointment.getDoctorId());
        item.put("slotId", appointment.getSlotId());
        item.put("patientName", appointment.getPatientName());
        item.put("phone", appointment.getPhone());
        item.put("email", appointment.getEmail());
        item.put("notes", appointment.getNotes());
        item.put("startsAt", Dates.format(appointment.getStartsAt()));
        item.put("endsAt", Dates.format(appointment.getEndsAt()));
        item.put("createdAt", Dates.format(appointment.getCreatedAt()));
        item.put("status", appointment.getStatus().name());
        return item;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> result = Json.object();
        result.put("error", message);
        return result;
    }

    private Double parseDouble(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new ValidationException("minRating має бути числом.");
        }
    }

    private BigDecimal parseMoney(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException("maxPrice має бути числом.");
        }
    }

    private Integer parseInteger(String value, String fieldName) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException(fieldName + " має бути цілим числом.");
        }
    }

    private String money(BigDecimal value) {
        return value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    private String lastSegment(String path) {
        int index = path.lastIndexOf('/');
        return index >= 0 ? decode(path.substring(index + 1)) : decode(path);
    }

    private String string(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (Strings.isBlank(rawQuery)) {
            return result;
        }
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            int equals = pair.indexOf('=');
            if (equals >= 0) {
                result.put(decode(pair.substring(0, equals)), decode(pair.substring(equals + 1)));
            } else {
                result.put(decode(pair), "");
            }
        }
        return result;
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (Exception ex) {
            return value;
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,DELETE,OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
}
