package com.nowfloats.NavigationDrawer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class OfferFloatsModel implements Parcelable{
    public String _id;
    public String message;
    public String messageDescription;
    public String createdOn;
    public String dealEndDate;
    public String dealStartDate;
    public String dealUri;
    public String discountPercent;
    public String url;
    public String tileImageUri;
    public String imageUri;
    public ArrayList<String> keywords;

    public OfferFloatsModel(Parcel in){
        _id = in.readString();
        message = in.readString();
        messageDescription = in.readString();
        createdOn = in.readString();
        dealEndDate = in.readString();
        dealStartDate = in.readString();
        dealUri = in.readString();
        discountPercent = in.readString();
        url = in.readString();
        tileImageUri = in.readString();
        imageUri = in.readString();
        keywords = new ArrayList<String>();
        in.readStringList(keywords);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(message);
        dest.writeString(messageDescription);
        dest.writeString(createdOn);
        dest.writeString(dealEndDate);
        dest.writeString(dealStartDate);
        dest.writeString(dealUri);
        dest.writeString(discountPercent);
        dest.writeString(url);
        dest.writeString(tileImageUri);
        dest.writeString(imageUri);

    }
    public static Creator<OfferFloatsModel> CREATOR = new Creator<OfferFloatsModel>() {

        @Override
        public OfferFloatsModel createFromParcel(Parcel source) {
            return new OfferFloatsModel(source);
        }

        @Override
        public OfferFloatsModel[] newArray(int size) {
            return new OfferFloatsModel[size];
        }

    };
}
