package ua.edu.duit.medical.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import ua.edu.duit.medical.exception.ValidationException;

public final class Dates {
    private Dates() {
    }

    public static LocalDate parseDate(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Дата має бути у форматі YYYY-MM-DD.");
        }
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }
}

