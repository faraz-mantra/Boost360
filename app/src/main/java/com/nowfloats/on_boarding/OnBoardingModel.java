package com.nowfloats.on_boarding;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Admin on 21-03-2018.
 */

public class OnBoardingModel implements Parcelable{

    private ArrayList<ScreenData> screenDataArrayList;

    protected OnBoardingModel(Parcel in) {
        screenDataArrayList = in.createTypedArrayList(ScreenData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(screenDataArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public ArrayList<ScreenData> getScreenDataArrayList() {
        return screenDataArrayList;
    }

    public void setScreenDataArrayList(ArrayList<ScreenData> screenDataArrayList) {
        this.screenDataArrayList = screenDataArrayList;
    }


    public static class ScreenData implements Parcelable{

        public String key;

        public boolean isVisible;

        public String value;

        protected ScreenData(Parcel in) {
            key = in.readString();
            isVisible = in.readByte() != 0;
            value = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(key);
            dest.writeByte((byte) (isVisible ? 1 : 0));
            dest.writeString(value);
        }

        @Override
        public int describeContents() {
            return 0;
        }

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

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setVisible(boolean visible) {
            isVisible = visible;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
