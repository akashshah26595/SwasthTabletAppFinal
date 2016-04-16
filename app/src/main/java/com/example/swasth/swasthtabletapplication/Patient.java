package com.example.swasth.swasthtabletapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mitali on 06-04-2016.
 */

public class Patient implements Parcelable {
    final int patient_id;
    final String fname;
    final String lname;

    public Patient(int patient_id, String fname, String lname) {
        this.patient_id = patient_id;
        this.fname = fname;
        this.lname = lname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.patient_id);
        dest.writeString(this.fname);
        dest.writeString(this.lname);
    }

    protected Patient(Parcel in) {
        this.patient_id = in.readInt();
        this.fname = in.readString();
        this.lname = in.readString();
    }

    public static final Parcelable.Creator<Patient> CREATOR = new Parcelable.Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel source) {
            return new Patient(source);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };
}
