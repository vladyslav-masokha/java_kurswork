package ua.edu.duit.medical.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Doctor {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String specialtyId;
    private final String city;
    private final String clinic;
    private final String address;
    private final double rating;
    private final int yearsExperience;
    private final BigDecimal consultationPrice;
    private final List<String> languages;
    private final List<AvailabilitySlot> availability;

    public Doctor(String id, String firstName, String lastName, String specialtyId, String city, String clinic, String address,
            double rating, int yearsExperience, BigDecimal consultationPrice, List<String> languages, List<AvailabilitySlot> availability) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialtyId = specialtyId;
        this.city = city;
        this.clinic = clinic;
        this.address = address;
        this.rating = rating;
        this.yearsExperience = yearsExperience;
        this.consultationPrice = consultationPrice;
        this.languages = new ArrayList<String>(languages);
        this.availability = new ArrayList<AvailabilitySlot>(availability);
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getSpecialtyId() {
        return specialtyId;
    }

    public String getCity() {
        return city;
    }

    public String getClinic() {
        return clinic;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public BigDecimal getConsultationPrice() {
        return consultationPrice;
    }

    public List<String> getLanguages() {
        return Collections.unmodifiableList(languages);
    }

    public List<AvailabilitySlot> getAvailability() {
        return Collections.unmodifiableList(availability);
    }
}

