package com.example.nycschool;

import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Controller that fetches the school directory data from
 * (1) https://data.cityofnewyork.us/Education/2017-DOE-High-School-Directory/s3k6-pzi2 and
 * (2) https://data.cityofnewyork.us/Education/2012-SAT-Results/f9bf-2cp4. The school directory
 * data will as a result contain schools that may only show up in one directory. In other words, SAT
 * data may be unavailable for some schools that only appear in (1). There also may be
 * missing data for schools that only appear in (2). This approach loads up all data in the
 * beginning instead of fetching SAT scores "lazily" only when selecting a school for more data.
 */
// @Singleton
// TODO(amanda): If allotted more time, this would be injected into the SchoolListFragment and it would be a singleton since we only need one instance of it.
public class SchoolDirectoryController {
    @VisibleForTesting
    static final String SCHOOL_DIRECTORY_JSON = "https://data.cityofnewyork.us/resource/s3k6-pzi2.json";
    @VisibleForTesting
    static final String SCHOOL_SAT_DATA_JSON = "https://data.cityofnewyork.us/resource/f9bf-2cp4.json";
    private boolean isInitialized = false;
    private SchoolDirectory schoolDirectory;

    // @Inject
    SchoolDirectoryController() {}

    /**
     * Should only initialize once. If there is an error loading the data, it will return
     * Optional.empty(). This method will fetch both the school data from
     */
    public Single<Optional<SchoolDirectory>> initialize(ScheduledExecutorService executor) {
        // Creating a synchronized block so that we don't accidentally access the isInitialized
        // variable while the task is still executing.
        synchronized (this) {
            if (isInitialized) {
                return Single.just(Optional.ofNullable(schoolDirectory));
            }

            // Note: The data fetches will not be done in parallel to avoid overwriting entries into
            // the school directory map. Instead, the fetches will be chained.
            return JsonFetcher
                    .getJsonArray(SCHOOL_DIRECTORY_JSON)
                    // Makes the network request on provided executor.
                    .subscribeOn(Schedulers.from(executor))
                    // Observer will receive the data on this executor. We will keep it as the same
                    // executor (should be a background one) since we still need to parse the data.
                    .observeOn(Schedulers.from(executor))
                    // First, generate the school directory map with the schools in the json
                    // resource above.
                    .map(jsonObject -> {
                        HashMap<String, School> schoolDirectory = new HashMap<>();
                        generateSchoolDirectoryFromJson(schoolDirectory, jsonObject);
                        isInitialized = true;
                        return schoolDirectory;
                    })
                    // Update the school directory map with SAT scores.
                    .flatMap(schoolDirectoryMap -> updateSchoolsWithSatData(schoolDirectoryMap))
                    .map(schoolDirectoryMap -> {
                        this.schoolDirectory = new SchoolDirectory(schoolDirectoryMap);
                        return Optional.of(this.schoolDirectory);
                    })
                    .onErrorReturn(error -> {
                        isInitialized = true;
                        // Empty result will signal to caller that there was an error.
                        return Optional.empty();
                    });
        }
    }

    private void generateSchoolDirectoryFromJson(
            HashMap<String, School> schoolDirectory, JSONArray directoryObject) {
        for (int i = 0; i < directoryObject.length(); i++) {

            JSONObject jsonObject;
            try {
                jsonObject = directoryObject.getJSONObject(i);

                School school = new School();
                // Set school data from school json object.
                String name = jsonObject.getString("school_name");
                school.setName(name);

                String overview = jsonObject.getString("overview_paragraph");
                if (overview.length() != 0) {
                    school.setOverview(overview);
                }

                schoolDirectory.put(name, school);
            } catch (JSONException e) {
                // Swallow. If allotted more time, we would have to figure out what we want to do
                // if 1. a single school parsing fails or 2. if all schools parsing fails.
            }
        }
    }

    private Single<HashMap<String, School>> updateSchoolsWithSatData(
            HashMap<String, School> schoolDirectoryMap) {
        return JsonFetcher
                .getJsonArray(SCHOOL_SAT_DATA_JSON)
                .map(jsonObject -> {
                    updateOrCreateSchoolWithSatDataFromJson(jsonObject, schoolDirectoryMap);
                    return schoolDirectoryMap;
                });
    }
    private void updateOrCreateSchoolWithSatDataFromJson(JSONArray satObject, HashMap<String, School> schoolDirectory) {
        for (int i = 0; i < satObject.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = satObject.getJSONObject(i);

                // Set school data from school json object.
                String name = jsonObject.getString("school_name");
                School school;
                if (schoolDirectory.containsKey(name)) {
                    school = schoolDirectory.get(name);
                } else {
                    // Add the school if it was not added yet.
                    school = new School();
                    school.setName(name);
                    schoolDirectory.put(name, school);
                }

                SchoolSatData satData = new SchoolSatData();

                // It's possible for the input for the expected integer is "s". We will in this case
                // not set the score if the data is not represented as an integer.
                getSatScore(jsonObject,"num_of_sat_test_takers").ifPresent(satData::setNumTestTakers);
                getSatScore(jsonObject, "sat_critical_reading_avg_score").ifPresent(satData::setAvgReadingScore);
                getSatScore(jsonObject, "sat_math_avg_score").ifPresent(satData::setAvgMathScore);
                getSatScore(jsonObject, "sat_writing_avg_score").ifPresent(satData::setAvgWritingScore);

                school.setSatData(satData);
            } catch (JSONException e) {
                // Swallow. If allotted more time, we would have to figure out what we want to do
                // if 1. a single school parsing fails or 2. if all schools parsing fails.
            }
        }
    }

    private Optional<Integer> getSatScore(JSONObject jsonObject, String scoreName) {
        try {
            return Optional.of(Integer.parseInt(jsonObject.getString(scoreName)));
        } catch (JSONException | NumberFormatException e) {
            return Optional.empty();
        }
    }

}
