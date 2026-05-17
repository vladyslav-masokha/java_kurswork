package ua.edu.duit.medical.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class DoctorSearchCriteria {
    private final String specialtyId;
    private final String city;
    private final String query;
    private final LocalDate date;
    private final Double minRating;
    private final BigDecimal maxPrice;
    private final Integer minExperience;
    private final String sort;

    public DoctorSearchCriteria(String specialtyId, String city, String query, LocalDate date, Double minRating,
            BigDecimal maxPrice, Integer minExperience, String sort) {
        this.specialtyId = specialtyId;
        this.city = city;
        this.query = query;
        this.date = date;
        this.minRating = minRating;
        this.maxPrice = maxPrice;
        this.minExperience = minExperience;
        this.sort = sort;
    }

    public String getSpecialtyId() {
        return specialtyId;
    }

    public String getCity() {
        return city;
    }

    public String getQuery() {
        return query;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getMinRating() {
        return minRating;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public String getSort() {
        return sort;
    }
}
