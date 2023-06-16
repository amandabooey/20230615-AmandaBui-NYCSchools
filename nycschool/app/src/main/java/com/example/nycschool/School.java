package com.example.nycschool;

import java.util.Optional;

/**
 * POJO that represents the school. It has setter and getter methods for school attributes.
 */
public class School {
    private String name;

    private SchoolSatData schoolSatData;

    private String overview;

    School() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setSatData(SchoolSatData schoolSatData) {
        this.schoolSatData = schoolSatData;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getName() {
        return name;
    }

    public Optional<SchoolSatData> getSatData() {
        return Optional.ofNullable(schoolSatData);
    }

    public Optional<String> getOverview() {
        return Optional.ofNullable(this.overview);
    }
}
