package com.example.nycschool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * POJO for the school directory. It contains a mapping from school name to the {@link School}.
 */
public class SchoolDirectory {
    private HashMap<String, School> schoolDirectory;

    SchoolDirectory(HashMap<String, School> schoolDirectory) {
        this.schoolDirectory = schoolDirectory;
    }

    public ArrayList<School> getSchools() {
        return new ArrayList<>(schoolDirectory.values());
    }

    /** Returns Optional.empty() if the school does not exist in the directory. Otherwise, returns
     * the {@link School} object that matches the school name.
     */
    public Optional<School> getSchoolData(String schoolName) {
        return Optional.ofNullable(schoolDirectory.get(schoolName));
    }
}
