package com.ru.test.issuedriver.taxi.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class place implements Parcelable {
    public String address;
    public double latitude;
    public double longtitude;

    @Exclude
    public boolean isSelected;

    public place(String address, double latitude, double longtitude) {
        this.address = address;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.isSelected = false;
    }

    public place() {
        this.isSelected = false;
    }

    public static final Creator<place> CREATOR = new Creator<place>() {
        @Override
        public place createFromParcel(Parcel in) {
            return new place(in);
        }

        @Override
        public place[] newArray(int size) {
            return new place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public place(Parcel in){
        address = in.readString();
        latitude = in.readDouble();
        longtitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longtitude);
    }
}
