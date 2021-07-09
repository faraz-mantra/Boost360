package com.nowfloats.sync.rmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.nowfloats.Login.Model.FloatsMessageModel;

import java.util.ArrayList;

/**
 * Created by NowFloats on 7/21/2016.
 */
public class SyncMessageModel implements Parcelable{

    public ArrayList<FloatsMessageModel> floats;

    public SyncMessageModel(Parcel in) {
        this.floats = new ArrayList<FloatsMessageModel>();
        in.readTypedList(floats, FloatsMessageModel.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeTypedList(floats);
    }

    public static final Creator<SyncMessageModel> CREATOR = new Creator<SyncMessageModel>() {

        public SyncMessageModel createFromParcel(Parcel in) {
            return new SyncMessageModel(in);
        }

        public SyncMessageModel[] newArray(int size) {
            return new SyncMessageModel[size];
        }
    };
}
