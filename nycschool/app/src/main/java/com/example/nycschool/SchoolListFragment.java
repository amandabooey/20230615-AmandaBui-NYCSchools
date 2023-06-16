package com.example.nycschool;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executors;

import rx.android.schedulers.AndroidSchedulers;

public class SchoolListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    SchoolDirectory schoolDirectory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.school_list_fragment, container, false);
        SchoolDirectoryController schoolDirectoryController = new SchoolDirectoryController();
        // Specify a new scheduler, so we don't fetch data on the main/UI thread.
        schoolDirectoryController.initialize(Executors.newScheduledThreadPool(1))
                // This will update the UI thread so make sure it receives the result on the UI thread.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        schoolDirectory -> {
                            if (!schoolDirectory.isPresent()) {
                                // TODO(amanda): If allotted more time, show error UI to replace the
                                //  loading spinner.
                            } else {
                                // Update the UI to replace the loading spinner (if loading spinner
                                // is implemented).
                                this.schoolDirectory = schoolDirectory.get();
                                ArrayList<School> schools = schoolDirectory.get().getSchools();
                                SchoolItemAdapter adapter =
                                        new SchoolItemAdapter(schools, getContext());
                                ListView listView = view.findViewById(android.R.id.list);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(this);
                            }
                        }
                );
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (view.findViewById(R.id.sat_data).getVisibility() == View.INVISIBLE) {
            expandItem(view);
        } else {
            collapseItem(view);
        }
    }

    /**
     * "Expands" the view by showing all the overview text and making the SAT score views visible.
     */
    private void expandItem(View view) {
        String schoolName = ((TextView) view.findViewById(R.id.school_name)).getText().toString();
        Optional<School> optionalSchool = schoolDirectory.getSchoolData(schoolName);
        if (!optionalSchool.isPresent()) {
            return;
        }

        // Show all of the overview text.
        TextView overview = view.findViewById(R.id.school_description);
        overview.setMaxLines(Integer.MAX_VALUE);
        overview.setEllipsize(null);

        setVisibilityForSatScores(view, /*isVisible=*/ true);
        Optional<SchoolSatData> schoolSatDataOptional = optionalSchool.get().getSatData();
        Integer readingScore = null;
        Integer mathScore = null;
        Integer writingScore = null;
        if (schoolSatDataOptional.isPresent()) {
            SchoolSatData schoolSatData = schoolSatDataOptional.get();
            readingScore = schoolSatData.getAvgReadingScore().orElse(null);
            mathScore = schoolSatData.getAvgMathScore().orElse(null);
            writingScore = schoolSatData.getAvgWritingScore().orElse(null);
        }
        setTextForSatScore(
                view.findViewById(R.id.critical_reading_score),
                R.string.average_critical_reading_score,
                readingScore);
        setTextForSatScore(
                view.findViewById(R.id.math_score),
                R.string.average_math_score,
                mathScore);
        setTextForSatScore(
                view.findViewById(R.id.writing_score),
                R.string.average_writing_score,
                writingScore);
    }

    private void setTextForSatScore(TextView view, int stringId, @Nullable Integer score) {
        String string = getResources().getText(stringId).toString() + " ";
        if (score == null) {
            string += getString(R.string.unavailable_score);
        } else {
            string += score;
        }
        view.setText(string);
    }

    /**
     * "Collapses" the view by hiding the SAT score views and truncating the overview text.
     */
    private void collapseItem(View view) {
        // Truncate the overview text.
        TextView overview = view.findViewById(R.id.school_description);
        overview.setMaxLines(2);
        overview.setEllipsize(TextUtils.TruncateAt.END);

        setVisibilityForSatScores(view, /*isVisible=*/ false);
    }

    private void setVisibilityForSatScores(View view, boolean isVisible) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.sat_data);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
        if (isVisible) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            constraintLayout.setLayoutParams(layoutParams);
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            layoutParams.height = 1;
            constraintLayout.setLayoutParams(layoutParams);
            constraintLayout.setVisibility(View.INVISIBLE);
        }

    }
}
