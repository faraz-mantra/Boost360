package com.nfx.leadmessages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 17-04-2017.
 */

public class CallLogModel implements Parcelable {

    public static final Creator<CallLogModel> CREATOR = new Creator<CallLogModel>() {
        @Override
        public CallLogModel createFromParcel(Parcel in) {
            return new CallLogModel(in);
        }

        @Override
        public CallLogModel[] newArray(int size) {
            return new CallLogModel[size];
        }
    };
    private String name, number, callType, date, duration_seconds;

    public CallLogModel() {

    }

    protected CallLogModel(Parcel in) {
        name = in.readString();
        number = in.readString();
        callType = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration_seconds() {
        return duration_seconds;
    }

    public void setDuration_seconds(String duration_seconds) {
        this.duration_seconds = duration_seconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(callType);
        dest.writeString(date);
        dest.writeString(duration_seconds);
    }

    @Override
    public String toString() {
        return name + " " + number + " " + callType + " " + duration_seconds + " " + date;
    }
}
