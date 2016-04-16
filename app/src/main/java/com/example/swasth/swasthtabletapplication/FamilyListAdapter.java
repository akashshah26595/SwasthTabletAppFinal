package com.example.swasth.swasthtabletapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class FamilyListAdapter extends BaseAdapter {

    public LayoutInflater mLayoutInflater;
    Patient[] mPatientList;
    private TextView patientFName;
    private TextView patientLName;
    private Button goButton;

    public FamilyListAdapter(Context context, Patient[] patients) {
        mLayoutInflater = LayoutInflater.from(context);
        mPatientList = patients;

    }

    @Override
    public int getCount() {
        return mPatientList.length;
    }

    @Override
    public Object getItem(int position) {
        return mPatientList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.family_list_row, parent, false);
        }

        patientFName = (TextView) convertView.findViewById(R.id.patientFName);
        patientLName = (TextView) convertView.findViewById(R.id.patientLName);

        patientFName.setText(mPatientList[position].fname);
        patientLName.setText(mPatientList[position].lname);

        //goButton = (Button) convertView.findViewById(R.id.goButton);
        return convertView;
    }
}
