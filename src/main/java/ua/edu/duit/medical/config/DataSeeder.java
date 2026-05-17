package ua.edu.duit.medical.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import ua.edu.duit.medical.domain.AvailabilitySlot;
import ua.edu.duit.medical.domain.Doctor;
import ua.edu.duit.medical.domain.Specialty;
import ua.edu.duit.medical.repository.DoctorRepository;
import ua.edu.duit.medical.repository.SpecialtyRepository;

public final class DataSeeder {
    private DataSeeder() {
    }

    public static void seed(SpecialtyRepository specialties, DoctorRepository doctors) {
        specialties.save(new Specialty("cardiology", "Кардіологія", "Діагностика та лікування серцево-судинних захворювань"));
        specialties.save(new Specialty("dermatology", "Дерматологія", "Консультації щодо захворювань шкіри, волосся та нігтів"));
        specialties.save(new Specialty("neurology", "Неврологія", "Порушення нервової системи, головний біль, біль у спині"));
        specialties.save(new Specialty("pediatrics", "Педіатрія", "Профілактика, діагностика та лікування дітей"));
        specialties.save(new Specialty("dentistry", "Стоматологія", "Профілактика і лікування захворювань зубів та ясен"));

        doctors.save(new Doctor(
                "doc-1",
                "Олена",
                "Коваленко",
                "cardiology",
                "Київ",
                "Медичний центр Pulse",
                "вул. Хрещатик, 24",
                4.9,
                12,
                new BigDecimal("850.00"),
                Arrays.asList("українська", "англійська"),
                slots("doc-1", "slot-", 1, LocalDate.now().plusDays(1), new int[] { 9, 10, 11, 14, 15 })));

        doctors.save(new Doctor(
                "doc-2",
                "Андрій",
                "Мельник",
                "dermatology",
                "Львів",
                "Клініка Dermalife",
                "просп. Свободи, 18",
                4.7,
                9,
                new BigDecimal("700.00"),
                Arrays.asList("українська", "польська"),
                slots("doc-2", "slot-", 6, LocalDate.now().plusDays(1), new int[] { 10, 12, 13, 16 })));

        doctors.save(new Doctor(
                "doc-3",
                "Ірина",
                "Савчук",
                "neurology",
                "Київ",
                "Нейроцентр Balance",
                "вул. Січових Стрільців, 52",
                4.8,
                15,
                new BigDecimal("950.00"),
                Arrays.asList("українська", "англійська"),
                slots("doc-3", "slot-", 10, LocalDate.now().plusDays(2), new int[] { 9, 11, 13, 15 })));

        doctors.save(new Doctor(
                "doc-4",
                "Марія",
                "Ткаченко",
                "pediatrics",
                "Одеса",
                "Дитяча клініка Сонечко",
                "вул. Дерибасівська, 9",
                4.6,
                8,
                new BigDecimal("650.00"),
                Arrays.asList("українська"),
                slots("doc-4", "slot-", 14, LocalDate.now().plusDays(1), new int[] { 8, 9, 12, 13, 17 })));

        doctors.save(new Doctor(
                "doc-5",
                "Віктор",
                "Гончар",
                "dentistry",
                "Дніпро",
                "Стоматологія SmilePro",
                "просп. Дмитра Яворницького, 70",
                4.5,
                11,
                new BigDecimal("780.00"),
                Arrays.asList("українська", "англійська"),
                slots("doc-5", "slot-", 20, LocalDate.now().plusDays(3), new int[] { 10, 11, 14, 15, 16 })));
    }

    private static List<AvailabilitySlot> slots(String doctorId, String prefix, int startIndex, LocalDate date, int[] hours) {
        AvailabilitySlot[] result = new AvailabilitySlot[hours.length];
        for (int i = 0; i < hours.length; i++) {
            LocalDateTime start = LocalDateTime.of(date, LocalTime.of(hours[i], 0));
            result[i] = new AvailabilitySlot(prefix + (startIndex + i), doctorId, start, start.plusMinutes(30));
        }
        return Arrays.asList(result);
    }
}

