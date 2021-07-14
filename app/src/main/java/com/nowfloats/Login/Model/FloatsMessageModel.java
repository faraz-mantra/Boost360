package com.nowfloats.Login.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guru on 26-05-2015.
 */
public class FloatsMessageModel implements Parcelable {
    public static final Parcelable.Creator<FloatsMessageModel> CREATOR = new Parcelable.Creator<FloatsMessageModel>() {

        public FloatsMessageModel createFromParcel(Parcel in) {
            return new FloatsMessageModel(in);
        }

        public FloatsMessageModel[] newArray(int size) {
            return new FloatsMessageModel[size];
        }
    };
    public String _id;
    public String createdOn;
    public String imageUri;
    public String message;
    public String tileImageUri;
    public String type;
    public String url;

    public FloatsMessageModel(Parcel in) {
        this._id = in.readString();
        this.createdOn = in.readString();
        this.imageUri = in.readString();
        this.message = in.readString();
        this.tileImageUri = in.readString();
        this.type = in.readString();
        this.url = in.readString();
    }

    public FloatsMessageModel(String id, String createdOn, String imageUri, String message, String tileImageUri, String type, String url) {
        this._id = id;
        this.createdOn = createdOn;
        this.imageUri = imageUri;
        this.message = message;
        this.tileImageUri = tileImageUri;
        this.type = type;
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(_id);
        parcel.writeString(createdOn);
        parcel.writeString(imageUri);
        parcel.writeString(message);
        parcel.writeString(tileImageUri);
        parcel.writeString(type);
        parcel.writeString(url);
    }
}
