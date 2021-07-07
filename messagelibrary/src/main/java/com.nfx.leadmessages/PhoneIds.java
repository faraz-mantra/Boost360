package com.nfx.leadmessages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NowFloats on 16-02-2017.
 */

class PhoneIds implements Parcelable {
    public static final Creator<PhoneIds> CREATOR = new Creator<PhoneIds>() {
        @Override
        public PhoneIds createFromParcel(Parcel in) {
            return new PhoneIds(in);
        }

        @Override
        public PhoneIds[] newArray(int size) {
            return new PhoneIds[size];
        }
    };
    public String phoneId, date;

    public PhoneIds() {

    }

    protected PhoneIds(Parcel in) {
        phoneId = in.readString();
        date = in.readString();
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return date + " " + phoneId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneId);
        dest.writeString(date);
    }
}
