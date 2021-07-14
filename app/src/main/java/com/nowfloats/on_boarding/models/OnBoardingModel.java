package com.nowfloats.on_boarding.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Admin on 21-03-2018.
 */

public class OnBoardingModel implements Parcelable {

    public static final Creator<OnBoardingModel> CREATOR = new Creator<OnBoardingModel>() {
        @Override
        public OnBoardingModel createFromParcel(Parcel in) {
            return new OnBoardingModel(in);
        }

        @Override
        public OnBoardingModel[] newArray(int size) {
            return new OnBoardingModel[size];
        }
    };
    private ArrayList<ScreenData> screenDataArrayList;
    private int toBeCompletePos = -1;

    public OnBoardingModel() {

    }

    protected OnBoardingModel(Parcel in) {
        screenDataArrayList = in.createTypedArrayList(ScreenData.CREATOR);
        toBeCompletePos = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(screenDataArrayList);
        dest.writeInt(toBeCompletePos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ArrayList<ScreenData> getScreenDataArrayList() {
        return screenDataArrayList;
    }

    public void setScreenDataArrayList(ArrayList<ScreenData> screenDataArrayList) {
        this.screenDataArrayList = screenDataArrayList;
    }

    public int getToBeCompletePos() {
        return toBeCompletePos;
    }

    public void setToBeCompletePos(int toBeCompletePos) {
        this.toBeCompletePos = toBeCompletePos;
    }

    public static class ScreenData implements Parcelable {

        public static final Creator<ScreenData> CREATOR = new Creator<ScreenData>() {
            @Override
            public ScreenData createFromParcel(Parcel in) {
                return new ScreenData(in);
            }

            @Override
            public ScreenData[] newArray(int size) {
                return new ScreenData[size];
            }
        };
        public String key;
        public boolean isComplete;
        public String value = "";

        public ScreenData() {

        }

        protected ScreenData(Parcel in) {
            key = in.readString();
            isComplete = in.readByte() != 0;
            value = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(key);
            dest.writeByte((byte) (isComplete ? 1 : 0));
            dest.writeString(value);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setIsComplete(boolean complete) {
            isComplete = complete;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
