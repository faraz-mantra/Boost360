package com.nowfloats.Login.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by guru on 26-05-2015.
 */
public class MessageModel implements Parcelable {
    public static final Parcelable.Creator<MessageModel> CREATOR = new Parcelable.Creator<MessageModel>() {

        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };
    public ArrayList<FloatsMessageModel> floats;
    public boolean moreFloatsAvailable;

    public MessageModel(Parcel in) {
        this.floats = new ArrayList<FloatsMessageModel>();
        in.readTypedList(floats, FloatsMessageModel.CREATOR);
        this.moreFloatsAvailable = (in.readInt() == 0) ? false : true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeTypedList(floats);
        parcel.writeInt(moreFloatsAvailable ? 1 : 0);
    }
}
