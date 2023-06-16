package com.example.nycschool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Optional;

public class SchoolItemAdapter extends ArrayAdapter<School> implements View.OnClickListener{

    private ArrayList<School> schools;
    Context context;

    private static class ViewHolder {
        TextView schoolName;
        TextView schoolDescription;
    }

    public SchoolItemAdapter(ArrayList<School> schools, Context context) {
        super(context, R.layout.school_item, schools);
        this.schools = schools;
        this.context = context;

    }

    @Override
    public void onClick(View v) {
        // Do nothing.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        School school = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.school_item, parent, false);
            viewHolder.schoolName = convertView.findViewById(R.id.school_name);
            viewHolder.schoolDescription = convertView.findViewById(R.id.school_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.schoolName.setText(school.getName());
        Optional<String> overview = school.getOverview();
        if (overview.isPresent()) {
            viewHolder.schoolDescription.setText(overview.get());
            viewHolder.schoolDescription.setVisibility(View.VISIBLE);
        } else {
            viewHolder.schoolDescription.setVisibility(View.GONE);
        }

        return convertView;
    }
}
